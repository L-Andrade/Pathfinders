package com.andradel.pathfinders.firebase.participant

import com.andradel.pathfinders.model.participant.NewParticipant
import com.andradel.pathfinders.model.participant.Participant
import javax.inject.Inject

class ParticipantMapper @Inject constructor() {
    fun toParticipant(id: String, fbParticipant: FirebaseParticipant): Participant {
        return Participant(
            id = id,
            name = fbParticipant.name,
            email = fbParticipant.email,
            participantClass = fbParticipant.scoutClass,
            birthday = fbParticipant.birthday
        )
    }

    fun toFirebaseParticipant(participant: NewParticipant): FirebaseParticipant {
        return FirebaseParticipant(
            name = participant.name,
            email = participant.email,
            scoutClass = participant.participantClass,
            birthday = participant.birthday
        )
    }
}