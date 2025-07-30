package com.andradel.pathfinders.shared.firebase.participant

import com.andradel.pathfinders.shared.extensions.throwCancellation
import com.andradel.pathfinders.shared.firebase.archiveChild
import com.andradel.pathfinders.shared.firebase.getMap
import com.andradel.pathfinders.shared.firebase.toFlow
import com.andradel.pathfinders.shared.firebase.toMapFlow
import com.andradel.pathfinders.shared.model.participant.NewParticipant
import com.andradel.pathfinders.shared.model.participant.Participant
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class ParticipantFirebaseDataSource(
    private val mapper: ParticipantMapper,
) {
    private val db = Firebase.database
    private fun participantsRef(archiveName: String?) = db.reference().archiveChild(archiveName, "participants")

    fun participants(archiveName: String?): Flow<List<Participant>> = participantsRef(archiveName)
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
            ref.child(key).setValue(mapper.toFirebaseParticipant(participant))
        }.throwCancellation()

    suspend fun deleteParticipant(participantId: String): Result<Unit> = runCatching {
        participantsRef(null).child(participantId).removeValue()
    }.throwCancellation()

    suspend fun isParticipantEmailRegistered(email: String, participantId: String?): Boolean {
        if (email.isBlank()) return false
        val participants = participantsRef(null).getMap<FirebaseParticipant>()
        return participants.any { it.value.email.equals(email, ignoreCase = true) && it.key != participantId }
    }

    suspend fun deleteParticipants(participantIds: List<String>): Result<Unit> = runCatching {
        participantsRef(null).updateChildren(participantIds.associateWith { null })
    }.throwCancellation()
}