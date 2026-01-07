package com.andradel.pathfinders.shared.model.participant

import com.andradel.pathfinders.flavors.model.ParticipantClass
import kotlinx.serialization.Serializable

@Serializable
data class SelectedParticipants(val participants: List<Participant>, val classes: List<ParticipantClass>)