package com.andradel.pathfinders.shared.features.activity.add.participant

import com.andradel.pathfinders.shared.model.ParticipantClass
import com.andradel.pathfinders.shared.model.participant.Participant
import kotlinx.serialization.Serializable

@Serializable
data class SelectedParticipants(
    val participants: List<Participant>,
    val classes: List<ParticipantClass>,
)