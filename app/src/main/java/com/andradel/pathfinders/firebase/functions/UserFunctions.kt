package com.andradel.pathfinders.firebase.functions

import com.andradel.pathfinders.extensions.throwCancellation
import com.andradel.pathfinders.firebase.awaitWithTimeout
import com.andradel.pathfinders.firebase.functions.model.FirebaseRoleRequest
import com.andradel.pathfinders.firebase.functions.model.FirebaseUser
import com.andradel.pathfinders.firebase.functions.model.Role
import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.user.User
import com.andradel.pathfinders.user.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UserFunctions @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    private val json: Json,
) {
    suspend fun getUser(): Result<User?> = runCatching {
        val user = auth.currentUser ?: return Result.success(null)
        val result = user.getIdToken(true).awaitWithTimeout()
        val userRole = when (result.claims["role"] as? String ?: "") {
            "admin" -> UserRole.Admin
            "class" -> {
                val classes = (result.claims["classes"] as? List<*>)?.mapNotNull { pClass ->
                    if (pClass is String) {
                        ParticipantClass.options.firstOrNull { it.name.equals(pClass, ignoreCase = true) }
                    } else {
                        null
                    }
                }.orEmpty()
                UserRole.ClassAdmin(classes.toSet())
            }

            else -> UserRole.User
        }
        return Result.success(User(user.displayName ?: "User", user.email, userRole))
    }.throwCancellation()

    fun signOut() {
        auth.signOut()
    }

    suspend fun getUsers(): Result<List<User>> = runCatching {
        val result = functions.getHttpsCallable("on_get_users").call().awaitWithTimeout()
        json.decodeFromString<List<FirebaseUser>>(result.data as String).map { fbUser ->
            User(
                name = fbUser.name ?: "User",
                email = fbUser.email,
                role = when (fbUser.role) {
                    Role.Admin -> UserRole.Admin
                    Role.ClassAdmin -> UserRole.ClassAdmin(fbUser.classes)
                    Role.User -> UserRole.User
                }
            )
        }
    }.throwCancellation()

    suspend fun setUserRole(email: String, userRole: UserRole): Result<Unit> = runCatching {
        val (role, classes) = when (userRole) {
            UserRole.Admin -> Role.Admin to null
            is UserRole.ClassAdmin -> Role.ClassAdmin to userRole.classes
            UserRole.User -> Role.User to null
        }
        val data = json.encodeToString(FirebaseRoleRequest(email, role, classes))
        functions.getHttpsCallable("on_set_user_role").call(data).awaitWithTimeout()
        Unit
    }.throwCancellation()
}