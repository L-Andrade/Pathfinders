package com.andradel.pathfinders.shared.features.admin.users

import com.andradel.pathfinders.shared.user.User

sealed interface AdminUserListScreenState {
    data object Loading : AdminUserListScreenState
    data object Error : AdminUserListScreenState
    data class Loaded(val users: List<User>) : AdminUserListScreenState
}