package com.andradel.pathfinders.shared.model.team

import com.andradel.pathfinders.shared.model.participant.Participant
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: String,
    val name: String,
    val participants: List<Participant>,
)