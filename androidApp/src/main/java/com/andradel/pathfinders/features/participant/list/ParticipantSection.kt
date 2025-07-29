package com.andradel.pathfinders.features.participant.list

import com.andradel.pathfinders.model.ParticipantClass

data class ParticipantSection(
    val participantClass: ParticipantClass,
    val participants: List<ParticipantWithTotalScore>,
    val collapsed: Boolean,
)