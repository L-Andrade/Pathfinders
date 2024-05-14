package com.andradel.pathfinders.firebase.participant

import com.andradel.pathfinders.model.participant.NewParticipant
import com.andradel.pathfinders.model.participant.Participant
import java.time.LocalDate
import javax.inject.Inject

class ParticipantMapper @Inject constructor() {
    fun toParticipant(id: String, fbParticipant: FirebaseParticipant, archived: Boolean): Participant {
        return Participant(
            id = id,
            name = fbParticipant.name,
            email = fbParticipant.email,
            contact = fbParticipant.contact,
            participantClass = fbParticipant.scoutClass,
            dateOfBirth = if (fbParticipant.dateOfBirth != null) LocalDate.parse(fbParticipant.dateOfBirth) else null,
            archived = archived,
        )
    }

    fun toFirebaseParticipant(participant: NewParticipant): FirebaseParticipant {
        return FirebaseParticipant(
            name = participant.name,
            email = participant.email,
            contact = participant.contact,
            scoutClass = participant.participantClass,
            dateOfBirth = participant.birthday
        )
    }

    fun toFirebaseParticipant(participant: Participant): FirebaseParticipant {
        return FirebaseParticipant(
            name = participant.name,
            email = participant.email,
            contact = participant.contact,
            scoutClass = participant.participantClass,
            dateOfBirth = participant.dateOfBirth?.toString()
        )
    }
}