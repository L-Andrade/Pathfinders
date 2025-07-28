package com.andradel.pathfinders.firebase.activity

import com.andradel.pathfinders.model.activity.ParticipantScores
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseActivity(
    val name: String = "",
    val date: String? = null,
    val participantIds: List<String> = emptyList(),
    val classes: List<String> = emptyList(),
    val criteriaIds: List<String> = emptyList(),
    val scores: ParticipantScores = emptyMap(),
)