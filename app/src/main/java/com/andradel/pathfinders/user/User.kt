package com.andradel.pathfinders.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface UserState {
    object Loading : UserState
    object Guest : UserState
}

@Parcelize
data class User(
    val name: String,
    val email: String?,
    val role: UserRole,
) : UserState, Parcelable