package com.andradel.pathfinders.features.admin

import com.andradel.pathfinders.user.User

sealed interface AdminScreenState {
    data object Loading : AdminScreenState
    data object Error : AdminScreenState
    data class Loaded(val users: List<User>) : AdminScreenState
}