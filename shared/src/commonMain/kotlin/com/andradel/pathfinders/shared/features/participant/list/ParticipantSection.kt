package com.andradel.pathfinders.shared.features.participant.list

import com.andradel.pathfinders.shared.model.ParticipantClass

data class ParticipantSection(
    val participantClass: ParticipantClass,
    val participants: List<ParticipantWithTotalScore>,
    val collapsed: Boolean,
)