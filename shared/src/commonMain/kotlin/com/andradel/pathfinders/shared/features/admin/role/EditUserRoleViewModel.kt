package com.andradel.pathfinders.shared.features.admin.role

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andradel.pathfinders.shared.features.admin.role.model.EditUserRole
import com.andradel.pathfinders.shared.firebase.functions.UserFunctions
import com.andradel.pathfinders.shared.model.ParticipantClass
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.customNavType
import com.andradel.pathfinders.shared.user.User
import com.andradel.pathfinders.shared.user.UserRole
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.reflect.typeOf


@KoinViewModel
class EditUserRoleViewModel(
    handle: SavedStateHandle,
    private val userFunctions: UserFunctions,
) : ViewModel() {
    private val user = handle.toRoute<NavigationRoute.EditUserRole>(
        typeMap = mapOf(typeOf<User>() to customNavType<User>())
    ).user

    private val role = MutableStateFlow(user.role.toEditable())
    private val selectedClasses = MutableStateFlow((user.role as? UserRole.ClassAdmin)?.classes)
    private val loading = MutableStateFlow(false)

    private val _result = Channel<Result<Unit>>()
    val result = _result.receiveAsFlow()

    val state = combine(role, selectedClasses, loading) { role, classes, loading ->
        val availableClasses = if (role == EditUserRole.ClassAdmin) classes.orEmpty() else null
        val valid = if (role == EditUserRole.ClassAdmin) !classes.isNullOrEmpty() else true
        EditUserRoleState(user.name, role, availableClasses, valid && !loading, loading)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EditUserRoleState(user.name, role.value, selectedClasses.value),
    )

    fun selectRole(role: EditUserRole) {
        this.role.value = role
    }

    fun selectClass(selectedClass: ParticipantClass) {
        val selected = selectedClasses.value.orEmpty()
        selectedClasses.value = if (selectedClass in selected) selected - selectedClass else selected + selectedClass
    }

    fun save() {
        val email = user.email ?: return
        loading.value = true
        viewModelScope.launch {
            _result.send(userFunctions.setUserRole(email, state.value.toUserRole()))
            loading.value = false
        }
    }

    private fun UserRole.toEditable(): EditUserRole = when (this) {
        UserRole.Admin -> EditUserRole.Admin
        is UserRole.ClassAdmin -> EditUserRole.ClassAdmin
        UserRole.User -> EditUserRole.User
    }

    private fun EditUserRoleState.toUserRole(): UserRole = when (role) {
        EditUserRole.Admin -> UserRole.Admin
        EditUserRole.ClassAdmin -> UserRole.ClassAdmin(classes.orEmpty())
        EditUserRole.User -> UserRole.User
    }
}