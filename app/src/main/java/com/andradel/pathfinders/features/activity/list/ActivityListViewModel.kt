package com.andradel.pathfinders.features.activity.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.model.activity.Activity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ActivityListViewModel @Inject constructor(
    private val dataSource: ActivityFirebaseDataSource
) : ViewModel() {
    fun deleteActivity(activity: Activity) {
        viewModelScope.launch {
            dataSource.deleteActivity(activityId = activity.id)
        }
    }

    val state = dataSource.activities.map { ActivityListState.Loaded(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActivityListState.Loading)
}