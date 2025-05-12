package com.andradel.pathfinders.firebase.activity

import androidx.annotation.Keep

@Keep
data class FirebaseActivityCriteria(
    val name: String = "",
    val maxScore: Int = 3,
)