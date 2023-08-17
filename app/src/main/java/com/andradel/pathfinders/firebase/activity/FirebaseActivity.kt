package com.andradel.pathfinders.firebase.activity

import androidx.annotation.Keep
import com.andradel.pathfinders.model.ScoutClass
import com.andradel.pathfinders.model.activity.ParticipantScores

@Keep
data class FirebaseActivity(
    val name: String = "",
    val date: String = "",
    val participantIds: List<String> = emptyList(),
    val classes: List<ScoutClass> = emptyList(),
    val criteriaIds: List<String> = emptyList(),
    val scores: ParticipantScores = emptyMap(),
)