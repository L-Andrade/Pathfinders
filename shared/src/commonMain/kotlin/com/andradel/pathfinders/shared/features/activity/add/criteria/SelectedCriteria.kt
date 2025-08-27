package com.andradel.pathfinders.shared.features.activity.add.criteria

import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import kotlinx.serialization.Serializable

@Serializable
data class SelectedCriteria(val selection: List<ActivityCriteria>)