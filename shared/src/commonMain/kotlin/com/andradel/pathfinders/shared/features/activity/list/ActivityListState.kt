package com.andradel.pathfinders.shared.features.activity.list

import com.andradel.pathfinders.shared.model.activity.Activity

sealed interface ActivityListState {
    object Loading : ActivityListState
    data class Loaded(val activities: List<Activity>, val canAdd: Boolean, val canDelete: Boolean) : ActivityListState
}