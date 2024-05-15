package com.andradel.pathfinders.firebase.participant

import com.andradel.pathfinders.extensions.throwCancellation
import com.andradel.pathfinders.firebase.archiveChild
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
    private val db: FirebaseDatabase,
    private val mapper: ParticipantMapper,
) {
    private fun participantsRef(archiveName: String?) = db.reference.archiveChild(archiveName, "participants")

    fun participants(archiveName: String?): Flow<List<Participant>> = participantsRef(archiveName).ref
        .toMapFlow<FirebaseParticipant>().map { participantMap -> participantMap.toParticipants(archiveName) }

    fun participant(archiveName: String?, key: String): Flow<Participant?> =
        participantsRef(archiveName).child(key).toFlow<FirebaseParticipant>().map { (key, fbParticipant) ->
            mapper.toParticipant(key, fbParticipant, archiveName)
        }

    suspend fun participantByEmail(archiveName: String?, email: String): Participant? =
        participants(archiveName).firstOrNull()?.firstOrNull { participant ->
            email.equals(participant.email, ignoreCase = true)
        }

    private fun Map<String, FirebaseParticipant>.toParticipants(archiveName: String?) =
        map { (key, value) -> mapper.toParticipant(id = key, value, archiveName) }

    suspend fun addOrUpdateParticipant(participant: NewParticipant, participantId: String?): Result<Unit> =
        runCatching {
            val ref = participantsRef(null)
            val key = participantId ?: requireNotNull(ref.push().key)
            ref.child(key).setValue(mapper.toFirebaseParticipant(participant)).awaitWithTimeout()
            Unit
        }.throwCancellation()

    suspend fun deleteParticipant(participantId: String): Result<Unit> = runCatching {
        participantsRef(null).child(participantId).removeValue().awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun isParticipantEmailRegistered(email: String, participantId: String?): Boolean {
        if (email.isBlank()) return false
        val participants = participantsRef(null).getMap<FirebaseParticipant>()
        return participants.any { it.value.email.equals(email, ignoreCase = true) && it.key != participantId }
    }

    suspend fun deleteParticipants(participantIds: List<String>): Result<Unit> = runCatching {
        participantsRef(null).updateChildren(participantIds.associateWith { null }).awaitWithTimeout()
        Unit
    }.throwCancellation()
}