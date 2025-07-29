package com.andradel.pathfinders.features.participant.profile

import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.participant.Participant

sealed interface ParticipantProfileState {
    val participant: Participant

    data class Loading(override val participant: Participant) : ParticipantProfileState
    data class Loaded(
        override val participant: Participant,
        val activities: List<Activity>,
    ) : ParticipantProfileState
}