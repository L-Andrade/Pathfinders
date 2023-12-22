package com.andradel.pathfinders.features.activity.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.user.UserRole
import com.andradel.pathfinders.user.UserSession
import com.andradel.pathfinders.user.role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityListViewModel @Inject constructor(
    private val dataSource: ActivityFirebaseDataSource,
    userSession: UserSession,
) : ViewModel() {
    fun deleteActivity(activity: Activity) {
        viewModelScope.launch {
            dataSource.deleteActivity(activityId = activity.id)
        }
    }

    val state = combine(dataSource.activities, userSession.role) { activities, role ->
        ActivityListState.Loaded(
            activities = activities.asSequence()
                .let { sequence ->
                    when (role) {
                        UserRole.Admin -> sequence
                        is UserRole.ClassAdmin -> sequence.filter { it.classes.contains(role.participantClass) }
                        UserRole.User -> emptySequence()
                    }
                }
                .sortedByDescending { activity -> activity.date }
                .toList(),
            canAdd = role is UserRole.Admin,
            canDelete = role is UserRole.Admin,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActivityListState.Loading)
}