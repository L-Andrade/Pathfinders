package com.andradel.pathfinders.shared.firebase.functions

import com.andradel.pathfinders.shared.extensions.throwCancellation
import com.andradel.pathfinders.shared.firebase.functions.model.FirebaseRoleRequest
import com.andradel.pathfinders.shared.firebase.functions.model.FirebaseUser
import com.andradel.pathfinders.shared.firebase.functions.model.Role
import com.andradel.pathfinders.shared.firebase.toClass
import com.andradel.pathfinders.shared.model.ParticipantClass
import com.andradel.pathfinders.shared.user.User
import com.andradel.pathfinders.shared.user.UserRole
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.database.database
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory

@Factory
class UserFunctions(
    private val json: Json,
    private val coroutineScope: CoroutineScope,
) {
    private val auth = Firebase.auth
    private val functions = Firebase.functions

    private val db = Firebase.database
    private val tokensRef = db.reference("tokens")

    suspend fun getUser(): Result<User?> = runCatching {
        val user = auth.currentUser ?: return Result.success(null)
        val result = user.getIdTokenResult(true)
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
        Firebase.crashlytics.setUserId(user.uid)
        return Result.success(User(user.displayName ?: "User", user.email, userRole))
    }.throwCancellation()

    fun signOut() {
        coroutineScope.launch {
            auth.signOut()
        }
    }

    suspend fun getUsers(): Result<List<User>> = runCatching {
        functions.httpsCallable("on_get_users").invoke().data<List<FirebaseUser>>().map { fbUser ->
            User(
                name = fbUser.name ?: "User",
                email = fbUser.email,
                role = when (fbUser.role) {
                    Role.Admin -> UserRole.Admin
                    Role.ClassAdmin -> UserRole.ClassAdmin(fbUser.classes.map { it.toClass() }.toSet())
                    Role.User -> UserRole.User
                },
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
        functions.httpsCallable("on_set_user_role").invoke(data)
        Unit
    }.throwCancellation()

    suspend fun setUserToken(token: String) {
        val uid = auth.currentUser?.uid ?: return
        val previousTokens = (tokensRef.child(uid).valueEvents.first().value as? List<*>?)?.toSet().orEmpty()
        val new = previousTokens + token
        if (new != previousTokens) {
            tokensRef.child(uid).setValue(new.toList())
        }
    }
}