package com.andradel.pathfinders.shared.model.participant

import com.andradel.pathfinders.shared.model.ParticipantClass

data class NewParticipant(
    val name: String,
    val email: String?,
    val contact: String?,
    val participantClass: ParticipantClass,
    val birthday: String?,
)