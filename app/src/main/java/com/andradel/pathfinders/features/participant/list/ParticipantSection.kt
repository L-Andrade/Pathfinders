package com.andradel.pathfinders.features.participant.list

import com.andradel.pathfinders.model.ScoutClass

data class ParticipantSection(
    val scoutClass: ScoutClass,
    val participants: List<ParticipantWithTotalScore>,
    val collapsed: Boolean,
)