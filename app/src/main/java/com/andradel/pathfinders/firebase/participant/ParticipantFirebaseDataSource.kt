package com.andradel.pathfinders.firebase.participant

import com.andradel.pathfinders.extensions.throwCancellation
import com.andradel.pathfinders.firebase.awaitWithTimeout
import com.andradel.pathfinders.firebase.getMap
import com.andradel.pathfinders.firebase.toFlow
import com.andradel.pathfinders.firebase.toMapFlow
import com.andradel.pathfinders.model.participant.NewParticipant
import com.andradel.pathfinders.model.participant.Participant
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ParticipantFirebaseDataSource @Inject constructor(
    db: FirebaseDatabase,
    private val mapper: ParticipantMapper,
) {
    private val participantsRef = db.reference.child("participants")

    val participants: Flow<List<Participant>> =
        participantsRef.ref.toMapFlow<FirebaseParticipant>().map { participantMap -> participantMap.toParticipants() }

    fun participant(key: String): Flow<Participant?> =
        participantsRef.child(key).toFlow<FirebaseParticipant>().map { (key, fbParticipant) ->
            mapper.toParticipant(key, fbParticipant, archived = false)
        }

    suspend fun participantByEmail(email: String): Participant? =
        participants.firstOrNull()?.firstOrNull { participant -> email.equals(participant.email, ignoreCase = true) }

    private fun Map<String, FirebaseParticipant>.toParticipants() =
        map { (key, value) -> mapper.toParticipant(id = key, value, archived = false) }

    suspend fun addOrUpdateParticipant(participant: NewParticipant, participantId: String?): Result<Unit> =
        runCatching {
            val key = participantId ?: requireNotNull(participantsRef.push().key)
            participantsRef.child(key).setValue(mapper.toFirebaseParticipant(participant)).awaitWithTimeout()
            Unit
        }.throwCancellation()

    suspend fun deleteParticipant(participantId: String): Result<Unit> = runCatching {
        participantsRef.child(participantId).removeValue().awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun isParticipantEmailRegistered(email: String, participantId: String?): Boolean {
        if (email.isBlank()) return false
        val participants = participantsRef.getMap<FirebaseParticipant>()
        return participants.any { it.value.email.equals(email, ignoreCase = true) && it.key != participantId }
    }

    suspend fun deleteParticipants(participantIds: List<String>): Result<Unit> = runCatching {
        participantsRef.updateChildren(participantIds.associateWith { null }).awaitWithTimeout()
        Unit
    }.throwCancellation()
}