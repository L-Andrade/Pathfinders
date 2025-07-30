package com.andradel.pathfinders.shared.firebase.activity

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseActivityCriteria(
    val name: String = "",
    val maxScore: Int = 3,
)