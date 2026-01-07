package com.andradel.pathfinders.shared.firebase.activity

import com.andradel.pathfinders.shared.extensions.throwCancellation
import com.andradel.pathfinders.shared.firebase.archiveChild
import com.andradel.pathfinders.shared.firebase.getValue
import com.andradel.pathfinders.shared.firebase.participant.FirebaseParticipant
import com.andradel.pathfinders.shared.firebase.toMapFlow
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.model.activity.NewActivity
import com.andradel.pathfinders.shared.model.activity.ParticipantScores
import com.andradel.pathfinders.shared.model.activity.TeamScores
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class ActivityFirebaseDataSource(
    private val mapper: ActivityMapper,
) {
    private val db = Firebase.database
    private fun activitiesRef(archiveName: String?) = db.reference().archiveChild(archiveName, "activities")
    private fun participantsRef(archiveName: String?) = db.reference().archiveChild(archiveName, "participants")
    private fun criteriaRef(archiveName: String?) = db.reference().archiveChild(archiveName, "criteria")

    fun activities(archiveName: String?): Flow<List<Activity>> = combine(
        activitiesRef(archiveName).toMapFlow<FirebaseActivity>(),
        participantsRef(archiveName).toMapFlow<FirebaseParticipant>(),
        criteriaRef(archiveName).toMapFlow<FirebaseActivityCriteria>(),
    ) { activities, participants, criteria ->
        mapper.toActivities(activities, participants, criteria, archiveName)
    }

    fun activitiesForUser(archiveName: String?, userId: String): Flow<List<Activity>> =
        activities(archiveName).map { activities ->
            activities.filter { activity -> activity.participants.any { it.id == userId } }
        }

    fun activitiesForTeam(archiveName: String?, teamId: String): Flow<List<Activity>> =
        activities(archiveName).map { activities ->
            activities.filter { activity -> activity.teamScores.any { it.key == teamId } }
        }

    suspend fun addOrUpdateActivity(activity: NewActivity, activityId: String?): Result<Unit> = runCatching {
        val ref = activitiesRef(null)
        val key = activityId ?: requireNotNull(ref.push().key)
        val firebaseActivity = mapper.toFirebaseActivity(activity)
        ref.child(key).setValue(firebaseActivity)
    }.throwCancellation()

    suspend fun updateScores(activityId: String, scores: ParticipantScores): Result<Unit> = runCatching {
        val ref = activitiesRef(null)
        val activity = ref.child(activityId).getValue<FirebaseActivity>()
        val teamScores = activity?.teamScores?.mapValues { (_, participantScores) ->
            participantScores.mapValues { (participantId, criteriaScore) ->
                criteriaScore + scores[participantId]?.mapValues { (_, score) -> score }.orEmpty()
            }
        }.orEmpty()
        ref.child(activityId).setValue(activity?.copy(scores = scores, teamScores = teamScores))
    }.throwCancellation()

    suspend fun updateTeamScores(activityId: String, scores: TeamScores): Result<Unit> = runCatching {
        val ref = activitiesRef(null)
        val activity = ref.child(activityId).getValue<FirebaseActivity>()
        val participantScores = activity?.scores.orEmpty() + scores.values.fold(emptyMap()) { acc, map ->
            val allKeys = (acc.keys + map.keys).toSet()
            allKeys.associateWith { participantId ->
                val accCriteria = acc[participantId].orEmpty()
                val mapCriteria = map[participantId].orEmpty()
                val allCriteriaKeys = (accCriteria.keys + mapCriteria.keys).toSet()
                allCriteriaKeys.associateWith { criteriaId ->
                    accCriteria.getOrDefault(criteriaId, 0) + mapCriteria.getOrDefault(criteriaId, 0)
                }
            }
        }
        ref.child(activityId).setValue(activity?.copy(teamScores = scores, scores = participantScores))
    }.throwCancellation()

    suspend fun deleteActivity(activityId: String): Result<Unit> = runCatching {
        activitiesRef(null).child(activityId).removeValue()
    }.throwCancellation()

    suspend fun deleteActivities(activityIds: List<String>): Result<Unit> = runCatching {
        activitiesRef(null).updateChildren(activityIds.associateWith { null })
    }.throwCancellation()
}