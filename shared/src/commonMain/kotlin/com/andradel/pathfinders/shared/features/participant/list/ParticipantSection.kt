package com.andradel.pathfinders.shared.features.participant.list

import com.andradel.pathfinders.flavors.model.ParticipantClass

data class ParticipantSection(
    val participantClass: ParticipantClass,
    val participants: List<ParticipantWithTotalScore>,
    val collapsed: Boolean,
)