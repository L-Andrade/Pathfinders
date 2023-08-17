package com.andradel.pathfinders.user

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map


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
                val isAdmin = result.claims["role"] == "admin"
                _userState.value = User(user.displayName ?: "User", user.email, isAdmin)
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
    get() = userState.map { (it as? User)?.isAdmin == true }