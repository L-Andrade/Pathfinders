package com.andradel.pathfinders.shared.user

import kotlinx.serialization.Serializable

sealed interface UserState {
    object Loading : UserState
    object Error : UserState
    object Guest : UserState
}

@Serializable
data class User(
    val name: String,
    val email: String?,
    val role: UserRole,
) : UserState