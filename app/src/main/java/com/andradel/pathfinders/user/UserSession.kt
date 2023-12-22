package com.andradel.pathfinders.user

import com.andradel.pathfinders.model.ParticipantClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserSession @Inject constructor(
    private val auth: FirebaseAuth
) {
    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState = _userState.asStateFlow()

    init {
        updateUser()
    }

    fun updateUser() {
        val user = auth.currentUser
        if (user != null) {
            _userState.value = UserState.Loading
            user.getIdToken(true).addOnSuccessListener { result ->
                val userRole = when (val role = result.claims["role"] as? String ?: "") {
                    "admin" -> UserRole.Admin
                    else -> {
                        val pClass = ParticipantClass.options.firstOrNull { it.name.equals(role, ignoreCase = true) }
                        if (pClass != null) UserRole.ClassAdmin(pClass) else UserRole.User
                    }
                }
                _userState.value = User(user.displayName ?: "User", user.email, userRole)
            }
        } else {
            _userState.value = UserState.Guest
        }
    }

    fun signOut() {
        auth.signOut()
        _userState.value = UserState.Guest
    }
}

val UserSession.isAdmin: Flow<Boolean>
    get() = userState.map { (it as? User)?.role is UserRole.Admin }