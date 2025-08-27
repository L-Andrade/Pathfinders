package com.andradel.pathfinders.shared.features.activity.add.criteria

import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria

sealed interface AddCriteriaToActivityState {
    object Loading : AddCriteriaToActivityState
    data class Loaded(
        val selection: List<ActivityCriteria>,
        val criteria: List<ActivityCriteria>,
    ) : AddCriteriaToActivityState
}