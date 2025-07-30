package com.andradel.pathfinders.shared.model.participant

import com.andradel.pathfinders.shared.model.ParticipantClass
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val id: String,
    val name: String,
    val email: String?,
    val contact: String?,
    val participantClass: ParticipantClass,
    val dateOfBirth: LocalDate?,
    val archiveName: String?,
)