package com.andradel.pathfinders.features.activity.list

import com.andradel.pathfinders.model.activity.Activity

sealed interface ActivityListState {
    object Loading : ActivityListState
    data class Loaded(val activities: List<Activity>, val canAdd: Boolean, val canDelete: Boolean) : ActivityListState
}