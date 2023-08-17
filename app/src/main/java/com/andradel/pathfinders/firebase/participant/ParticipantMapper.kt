package com.andradel.pathfinders.firebase.participant

import com.andradel.pathfinders.model.participant.NewParticipant
import com.andradel.pathfinders.model.participant.Participant
import javax.inject.Inject

class ParticipantMapper @Inject constructor() {
    fun toParticipant(id: String, fbParticipant: FirebaseParticipant): Participant {
        return Participant(id, fbParticipant.name, fbParticipant.email, fbParticipant.scoutClass)
    }

    fun toFirebaseParticipant(participant: NewParticipant): FirebaseParticipant {
        return FirebaseParticipant(participant.name, participant.email, participant.scoutClass)
    }
}