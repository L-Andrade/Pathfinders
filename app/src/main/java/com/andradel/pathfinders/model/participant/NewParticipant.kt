package com.andradel.pathfinders.model.participant

import com.andradel.pathfinders.model.ParticipantClass

data class NewParticipant(
    val name: String,
    val email: String?,
    val contact: String?,
    val participantClass: ParticipantClass,
    val birthday: String?
)