package com.andradel.pathfinders.shared.user

import com.andradel.pathfinders.shared.firebase.functions.UserFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class UserSession(
    private val userFunctions: UserFunctions,
    private val coroutineScope: CoroutineScope,
) {
    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState = _userState.asStateFlow()

    init {
        coroutineScope.launch {
            updateUser()
        }
    }

    suspend fun updateUser() {
        _userState.value = UserState.Loading
        userFunctions.getUser().onSuccess { user ->
            if (user != null) {
                _userState.value = user
            } else {
                _userState.value = UserState.Guest
            }
        }.onFailure {
            _userState.value = UserState.Error
        }
    }

    fun signOut() {
        userFunctions.signOut()
        _userState.value = UserState.Guest
    }

    fun setUserToken(token: String) {
        coroutineScope.launch {
            userFunctions.setUserToken(token)
        }
    }
}

val UserSession.isAdmin: Flow<Boolean>
    get() = userState.map { (it as? User)?.role is UserRole.Admin }

val UserSession.role: Flow<UserRole>
    get() = userState.mapNotNull { (it as? User)?.role }