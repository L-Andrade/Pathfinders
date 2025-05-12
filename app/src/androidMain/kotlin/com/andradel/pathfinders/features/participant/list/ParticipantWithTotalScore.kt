package com.andradel.pathfinders.features.participant.list

import com.andradel.pathfinders.model.participant.Participant

data class ParticipantWithTotalScore(
    val participant: Participant,
    val score: Int,
)