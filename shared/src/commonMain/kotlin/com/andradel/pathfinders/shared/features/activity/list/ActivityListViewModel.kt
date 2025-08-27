package com.andradel.pathfinders.shared.features.activity.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andradel.pathfinders.shared.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.user.UserRole
import com.andradel.pathfinders.shared.user.UserSession
import com.andradel.pathfinders.shared.user.role
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ActivityListViewModel(
    handle: SavedStateHandle,
    private val dataSource: ActivityFirebaseDataSource,
    userSession: UserSession,
) : ViewModel() {
    private val archiveName = handle.toRoute<NavigationRoute.ActivityList>().archiveName

    val state = combine(dataSource.activities(archiveName), userSession.role) { activities, role ->
        ActivityListState.Loaded(
            activities = activities.asSequence()
                .let { sequence ->
                    when (role) {
                        UserRole.Admin -> sequence
                        is UserRole.ClassAdmin -> sequence.filter { it.classes.any { c -> c in role.classes } }
                        UserRole.User -> emptySequence()
                    }
                }
                .sortedByDescending { activity -> activity.date }
                .toList(),
            canAdd = role is UserRole.Admin && archiveName == null,
            canDelete = role is UserRole.Admin && archiveName == null,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActivityListState.Loading)

    fun deleteActivity(activity: Activity) {
        viewModelScope.launch {
            dataSource.deleteActivity(activityId = activity.id)
        }
    }
}