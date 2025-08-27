package com.andradel.pathfinders.shared.features.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.shared.firebase.functions.UserFunctions
import com.andradel.pathfinders.shared.user.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AdminUserListViewModel(private val userFunctions: UserFunctions) : ViewModel() {
    private val users = MutableSharedFlow<Result<List<User>>>()
    private val loading = MutableStateFlow(false)
    val state = combine(users, loading) { result, loading ->
        if (loading) {
            AdminUserListScreenState.Loading
        } else {
            val items = result.getOrNull()
            if (items != null) AdminUserListScreenState.Loaded(items) else AdminUserListScreenState.Error
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminUserListScreenState.Loading)

    fun loadUsers() {
        loading.value = true
        viewModelScope.launch {
            users.emit(userFunctions.getUsers())
            loading.value = false
        }
    }
}