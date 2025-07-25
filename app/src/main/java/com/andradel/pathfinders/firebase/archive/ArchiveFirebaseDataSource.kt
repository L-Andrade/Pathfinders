package com.andradel.pathfinders.firebase.archive

import com.andradel.pathfinders.extensions.throwCancellation
import com.andradel.pathfinders.firebase.activity.ActivityCriteriaMapper
import com.andradel.pathfinders.firebase.activity.ActivityMapper
import com.andradel.pathfinders.firebase.exists
import com.andradel.pathfinders.firebase.participant.ParticipantMapper
import com.andradel.pathfinders.firebase.toMapFlow
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.archive.Archive
import com.andradel.pathfinders.model.criteria.ActivityCriteria
import com.andradel.pathfinders.model.participant.Participant
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Factory

@Factory
class ArchiveFirebaseDataSource(
    db: FirebaseDatabase,
    private val activityMapper: ActivityMapper,
    private val participantMapper: ParticipantMapper,
    private val criteriaMapper: ActivityCriteriaMapper,
) {
    private val archiveRef = db.reference.child("archive")

    val archive: Flow<List<Archive>> = archiveRef.toMapFlow<FirebaseArchive>().map { criteriaMap ->
        criteriaMap.map { (key, value) ->
            Archive(
                name = key,
                activities = activityMapper.toActivities(
                    activities = value.activities,
                    participants = value.participants,
                    criteria = value.criteria,
                    archiveName = key,
                ),
                participants = value.participants.map {
                    participantMapper.toParticipant(
                        id = it.key,
                        fbParticipant = it.value,
                        archiveName = key,
                    )
                },
                criteria = value.criteria.map { criteriaMapper.toCriteria(it.key, it.value) },
            )
        }
    }

    suspend fun addArchive(
        name: String,
        activities: List<Activity>,
        participants: List<Participant>,
        criteria: List<ActivityCriteria>,
    ): Result<Unit> = runCatching {
        if (archiveRef.child(name).exists()) {
            return Result.failure(IllegalArgumentException("Archive name already exists"))
        }
        archiveRef.child(name).setValue(
            FirebaseArchive(
                activities = activities.associate { it.id to activityMapper.toFirebaseActivity(it) },
                criteria = criteria.associate { it.id to criteriaMapper.toFirebaseCriteria(it) },
                participants = participants.associate { it.id to participantMapper.toFirebaseParticipant(it) },
            ),
        ).await()
        Unit
    }.throwCancellation()

    suspend fun deleteArchive(name: String) = runCatching {
        archiveRef.child(name).removeValue().await()
        Unit
    }.throwCancellation()
}