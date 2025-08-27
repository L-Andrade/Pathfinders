package com.andradel.pathfinders.shared.features.participant.list

import com.andradel.pathfinders.shared.model.participant.Participant

data class ParticipantWithTotalScore(
    val participant: Participant,
    val score: Int,
)