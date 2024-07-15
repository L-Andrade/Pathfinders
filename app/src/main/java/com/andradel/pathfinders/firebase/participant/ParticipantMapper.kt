package com.andradel.pathfinders.firebase.participant

import com.andradel.pathfinders.firebase.toClass
import com.andradel.pathfinders.model.participant.NewParticipant
import com.andradel.pathfinders.model.participant.Participant
import java.time.LocalDate
import javax.inject.Inject

class ParticipantMapper @Inject constructor() {
    fun toParticipant(id: String, fbParticipant: FirebaseParticipant, archiveName: String?): Participant {
        return Participant(
            id = id,
            name = fbParticipant.name,
            email = fbParticipant.email,
            contact = fbParticipant.contact,
            participantClass = fbParticipant.scoutClass.toClass(),
            dateOfBirth = if (fbParticipant.dateOfBirth != null) LocalDate.parse(fbParticipant.dateOfBirth) else null,
            archiveName = archiveName,
        )
    }

    fun toFirebaseParticipant(participant: NewParticipant): FirebaseParticipant {
        return FirebaseParticipant(
            name = participant.name,
            email = participant.email,
            contact = participant.contact,
            scoutClass = participant.participantClass.name,
            dateOfBirth = participant.birthday,
        )
    }

    fun toFirebaseParticipant(participant: Participant): FirebaseParticipant {
        return FirebaseParticipant(
            name = participant.name,
            email = participant.email,
            contact = participant.contact,
            scoutClass = participant.participantClass.name,
            dateOfBirth = participant.dateOfBirth?.toString(),
        )
    }
}