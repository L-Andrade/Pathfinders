package com.andradel.pathfinders.firebase.participant

import com.andradel.pathfinders.firebase.getMap
import com.andradel.pathfinders.firebase.toFlow
import com.andradel.pathfinders.firebase.toMapFlow
import com.andradel.pathfinders.model.participant.NewParticipant
import com.andradel.pathfinders.model.participant.Participant
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ParticipantFirebaseDataSource @Inject constructor(
    db: FirebaseDatabase,
    private val mapper: ParticipantMapper,
) {
    private val participantsRef = db.reference.child("participants")

    val participants: Flow<List<Participant>> =
        participantsRef.ref.toMapFlow<FirebaseParticipant>().map { participantMap -> participantMap.toParticipants() }

    fun participant(key: String): Flow<Participant?> =
        participantsRef.child(key).toFlow<FirebaseParticipant>().map { (key, fbParticipant) ->
            mapper.toParticipant(key, fbParticipant)
        }

    suspend fun participantByEmail(email: String): Participant? =
        participants.firstOrNull()?.firstOrNull { participant -> email.equals(participant.email, ignoreCase = true) }

    private fun Map<String, FirebaseParticipant>.toParticipants() =
        map { (key, value) -> mapper.toParticipant(id = key, value) }

    suspend fun addOrUpdateParticipant(participant: NewParticipant, participantId: String?) {
        val key = participantId ?: requireNotNull(participantsRef.push().key)
        participantsRef.child(key).setValue(mapper.toFirebaseParticipant(participant)).await()
    }

    suspend fun deleteParticipant(participantId: String) {
        participantsRef.child(participantId).removeValue().await()
    }

    suspend fun isParticipantEmailRegistered(email: String, participantId: String?): Boolean {
        if (email.isBlank()) return false
        val participants = participantsRef.getMap<FirebaseParticipant>()
        return participants.any { it.value.email.equals(email, ignoreCase = true) && it.key != participantId }
    }
}