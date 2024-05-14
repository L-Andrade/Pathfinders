package com.andradel.pathfinders.firebase.activity

import com.andradel.pathfinders.extensions.throwCancellation
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
    db: FirebaseDatabase,
    private val mapper: ActivityMapper,
) {
    private val activitiesRef = db.reference.child("activities")
    private val participantsRef = db.reference.child("participants")
    private val criteriaRef = db.reference.child("criteria")

    val activities: Flow<List<Activity>> =
        combine(
            activitiesRef.ref.toMapFlow<FirebaseActivity>(),
            participantsRef.ref.toMapFlow<FirebaseParticipant>(),
            criteriaRef.ref.toMapFlow<FirebaseActivityCriteria>(),
        ) { activities, participants, criteria ->
            mapper.toActivities(activities, participants, criteria, archived = false)
        }

    fun activitiesForUser(userId: String): Flow<List<Activity>> = activities.map { activities ->
        activities.filter { activity -> activity.participants.any { it.id == userId } }
    }

    suspend fun addOrUpdateActivity(activity: NewActivity, activityId: String?): Result<Unit> = runCatching {
        val key = activityId ?: requireNotNull(activitiesRef.push().key)
        val firebaseActivity = mapper.toFirebaseActivity(activity)
        activitiesRef.child(key).setValue(firebaseActivity).awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun updateScores(activityId: String, scores: ParticipantScores): Result<Unit> = runCatching {
        val activity = activitiesRef.child(activityId).getValue<FirebaseActivity>()
        activitiesRef.child(activityId).setValue(activity.copy(scores = scores)).awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun deleteActivity(activityId: String): Result<Unit> = runCatching {
        activitiesRef.child(activityId).removeValue().awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun deleteActivities(activityIds: List<String>): Result<Unit> = runCatching {
        activitiesRef.updateChildren(activityIds.associateWith { null }).awaitWithTimeout()
        Unit
    }.throwCancellation()
}
