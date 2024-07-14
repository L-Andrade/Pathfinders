package com.andradel.pathfinders.firebase.activity

import com.andradel.pathfinders.extensions.throwCancellation
import com.andradel.pathfinders.firebase.archiveChild
import com.andradel.pathfinders.firebase.awaitWithTimeout
import com.andradel.pathfinders.firebase.getValue
import com.andradel.pathfinders.firebase.participant.FirebaseParticipant
import com.andradel.pathfinders.firebase.toMapFlow
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.activity.NewActivity
import com.andradel.pathfinders.model.activity.ParticipantScores
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityFirebaseDataSource @Inject constructor(
    private val db: FirebaseDatabase,
    private val mapper: ActivityMapper,
) {
    private fun activitiesRef(archiveName: String?) = db.reference.archiveChild(archiveName, "activities")
    private fun participantsRef(archiveName: String?) = db.reference.archiveChild(archiveName, "participants")
    private fun criteriaRef(archiveName: String?) = db.reference.archiveChild(archiveName, "criteria")

    fun activities(archiveName: String?): Flow<List<Activity>> =
        combine(
            activitiesRef(archiveName).ref.toMapFlow<FirebaseActivity>(),
            participantsRef(archiveName).ref.toMapFlow<FirebaseParticipant>(),
            criteriaRef(archiveName).ref.toMapFlow<FirebaseActivityCriteria>(),
        ) { activities, participants, criteria ->
            mapper.toActivities(activities, participants, criteria, archiveName)
        }

    fun activitiesForUser(archiveName: String?, userId: String): Flow<List<Activity>> =
        activities(archiveName).map { activities ->
            activities.filter { activity -> activity.participants.any { it.id == userId } }
        }

    suspend fun addOrUpdateActivity(activity: NewActivity, activityId: String?): Result<Unit> = runCatching {
        val ref = activitiesRef(null)
        val key = activityId ?: requireNotNull(ref.push().key)
        val firebaseActivity = mapper.toFirebaseActivity(activity)
        ref.child(key).setValue(firebaseActivity).awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun updateScores(activityId: String, scores: ParticipantScores): Result<Unit> = runCatching {
        val ref = activitiesRef(null)
        val activity = ref.child(activityId).getValue<FirebaseActivity>()
        ref.child(activityId).setValue(activity.copy(scores = scores)).awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun deleteActivity(activityId: String): Result<Unit> = runCatching {
        activitiesRef(null).child(activityId).removeValue().awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun deleteActivities(activityIds: List<String>): Result<Unit> = runCatching {
        activitiesRef(null).updateChildren(activityIds.associateWith { null }).awaitWithTimeout()
        Unit
    }.throwCancellation()
}
