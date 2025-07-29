package com.andradel.pathfinders.features.activity.add.participant

import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.participant.Participant

sealed interface AddParticipantsToActivityState {
    object Loading : AddParticipantsToActivityState
    data class Loaded(
        val selection: List<Participant>,
        val participants: List<Participant>,
        val filteringByClass: Boolean,
        val classes: List<ParticipantClass>,
    ) : AddParticipantsToActivityState
}