package com.andradel.pathfinders.shared.firebase.activity

import com.andradel.pathfinders.shared.model.activity.ParticipantScores
import com.andradel.pathfinders.shared.model.activity.TeamScores
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseActivity(
    val name: String = "",
    val date: String? = null,
    val participantIds: List<String> = emptyList(),
    val classes: List<String> = emptyList(),
    val criteriaIds: List<String> = emptyList(),
    val scores: ParticipantScores = emptyMap(),
    val teamScores: TeamScores = emptyMap(),
)