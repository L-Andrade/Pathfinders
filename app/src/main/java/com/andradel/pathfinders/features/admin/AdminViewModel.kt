package com.andradel.pathfinders.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.firebase.functions.UserFunctions
import com.andradel.pathfinders.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userFunctions: UserFunctions,
) : ViewModel() {
    private val users = MutableSharedFlow<Result<List<User>>>()
    private val loading = MutableStateFlow(false)
    val state = combine(users, loading) { result, loading ->
        if (loading) {
            AdminScreenState.Loading
        } else {
            val items = result.getOrNull()
            if (items != null) AdminScreenState.Loaded(items) else AdminScreenState.Error
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminScreenState.Loading)

    fun loadUsers() {
        loading.value = true
        viewModelScope.launch {
            users.emit(userFunctions.getUsers())
            loading.value = false
        }
    }
}