package com.andradel.pathfinders.features.activity.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.user.UserRole
import com.andradel.pathfinders.user.UserSession
import com.andradel.pathfinders.user.role
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
    private val archiveName = handle.get<String?>("archiveName")

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