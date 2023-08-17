package com.andradel.pathfinders.user

sealed interface UserState {
    object Loading : UserState
    object Guest : UserState
}

data class User(
    val name: String,
    val email: String?,
    val isAdmin: Boolean,
) : UserState