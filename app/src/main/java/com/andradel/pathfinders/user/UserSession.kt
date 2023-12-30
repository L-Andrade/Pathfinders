package com.andradel.pathfinders.user

import com.andradel.pathfinders.firebase.functions.UserFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor(
    private val userFunctions: UserFunctions,
    coroutineScope: CoroutineScope,
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
        val user = userFunctions.getUser()
        if (user != null) {
            _userState.value = user
        } else {
            _userState.value = UserState.Guest
        }
    }

    fun signOut() {
        userFunctions.signOut()
        _userState.value = UserState.Guest
    }
}

val UserSession.isAdmin: Flow<Boolean>
    get() = userState.map { (it as? User)?.role is UserRole.Admin }

val UserSession.role: Flow<UserRole>
    get() = userState.mapNotNull { (it as? User)?.role }