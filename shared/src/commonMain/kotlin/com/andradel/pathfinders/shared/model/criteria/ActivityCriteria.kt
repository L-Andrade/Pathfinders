package com.andradel.pathfinders.shared.model.criteria

import kotlinx.serialization.Serializable

@Serializable
data class ActivityCriteria(
    val id: String,
    val name: String,
    val maxScore: Int,
)