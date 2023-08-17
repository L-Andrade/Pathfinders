package com.andradel.pathfinders.features.activity.add.criteria

import com.andradel.pathfinders.model.activity.ActivityCriteria

sealed interface AddCriteriaToActivityState {
    object Loading : AddCriteriaToActivityState
    data class Loaded(
        val selection: List<ActivityCriteria>,
        val criteria: List<ActivityCriteria>,
    ) : AddCriteriaToActivityState
}