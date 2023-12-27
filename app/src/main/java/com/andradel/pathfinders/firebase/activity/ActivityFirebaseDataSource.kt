package com.andradel.pathfinders.firebase.activity

import com.andradel.pathfinders.firebase.getValue
import com.andradel.pathfinders.firebase.participant.FirebaseParticipant
import com.andradel.pathfinders.firebase.participant.ParticipantMapper
import com.andradel.pathfinders.firebase.toMapFlow
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.activity.ActivityCriteria
import com.andradel.pathfinders.model.activity.NewActivity
import com.andradel.pathfinders.model.activity.ParticipantScores
import com.andradel.pathfinders.model.participant.Participant
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class ActivityFirebaseDataSource @Inject constructor(
    db: FirebaseDatabase,
    private val mapper: ParticipantMapper,
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
            activities.toActivities(participants, criteria)
        }

    fun activitiesForUser(userId: String): Flow<List<Activity>> = activities.map { activities ->
        activities.filter { activity -> activity.participants.any { it.id == userId } }
    }

    suspend fun addOrUpdateActivity(activity: NewActivity, activityId: String?) {
        val key = activityId ?: requireNotNull(activitiesRef.push().key)
        val firebaseActivity = with(activity) {
            FirebaseActivity(
                name = name,
                date = date,
                participantIds = participants.map { it.id },
                classes = classes,
                criteriaIds = criteria.map { it.id },
                scores = activity.scores.buildWith(participants, criteria),
            )
        }
        activitiesRef.child(key).setValue(firebaseActivity).await()
    }

    private fun ParticipantScores.buildWith(
        participants: List<Participant>,
        criteria: List<ActivityCriteria>
    ): ParticipantScores {
        return buildMap {
            participants.forEach { participant ->
                put(
                    participant.id,
                    buildMap { criteria.forEach { c -> put(c.id, this@buildWith[participant.id]?.get(c.id) ?: 0) } }
                )
            }
        }
    }

    suspend fun updateScores(activityId: String, scores: ParticipantScores) {
        val activity = activitiesRef.child(activityId).getValue<FirebaseActivity>()
        activitiesRef.child(activityId).setValue(activity.copy(scores = scores)).await()
    }

    suspend fun deleteActivity(activityId: String) {
        activitiesRef.child(activityId).removeValue().await()
    }

    private fun Map<String, FirebaseActivity>.toActivities(
        participants: Map<String, FirebaseParticipant>,
        criteria: Map<String, FirebaseActivityCriteria>,
    ): List<Activity> {
        return map { (key, value) ->
            Activity(
                key,
                value.name,
                if (!value.date.isNullOrBlank()) LocalDate.parse(value.date) else null,
                value.participantIds.mapNotNull { id -> participants[id]?.let { mapper.toParticipant(id, it) } },
                value.classes,
                value.criteriaIds.mapNotNull { id -> criteria[id]?.let { ActivityCriteria(id, it.name, it.maxScore) } },
                value.scores
            )
        }
    }
}
