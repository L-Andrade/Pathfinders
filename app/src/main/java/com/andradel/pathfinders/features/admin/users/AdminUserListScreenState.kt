package com.andradel.pathfinders.features.admin.users

import com.andradel.pathfinders.user.User

sealed interface AdminUserListScreenState {
    data object Loading : AdminUserListScreenState
    data object Error : AdminUserListScreenState
    data class Loaded(val users: List<User>) : AdminUserListScreenState
}