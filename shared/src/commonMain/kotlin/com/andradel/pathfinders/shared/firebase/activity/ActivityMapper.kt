package com.andradel.pathfinders.shared.firebase.activity

import com.andradel.pathfinders.shared.firebase.participant.FirebaseParticipant
import com.andradel.pathfinders.shared.firebase.participant.ParticipantMapper
import com.andradel.pathfinders.shared.firebase.toClass
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.model.activity.NewActivity
import com.andradel.pathfinders.shared.model.activity.ParticipantScores
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import com.andradel.pathfinders.shared.model.participant.Participant
import kotlinx.datetime.LocalDate
import org.koin.core.annotation.Factory

@Factory
class ActivityMapper(
    private val criteriaMapper: ActivityCriteriaMapper,
    private val participantMapper: ParticipantMapper,
) {
    fun toActivities(
        activities: Map<String, FirebaseActivity>,
        participants: Map<String, FirebaseParticipant>,
        criteria: Map<String, FirebaseActivityCriteria>,
        archiveName: String?,
    ): List<Activity> {
        return activities.map { (key, value) ->
            Activity(
                key,
                value.name,
                if (!value.date.isNullOrBlank()) LocalDate.parse(value.date) else null,
                value.participantIds.mapNotNull { id ->
                    participants[id]?.let { participantMapper.toParticipant(id, it, archiveName) }
                },
                value.classes.map { it.toClass() },
                value.criteriaIds.mapNotNull { id -> criteria[id]?.let { criteriaMapper.toCriteria(id, it) } },
                value.scores,
                value.teamScores,
                archiveName,
            )
        }
    }

    fun toFirebaseActivity(value: Activity): FirebaseActivity {
        return with(value) {
            FirebaseActivity(
                name = name,
                date = date?.toString(),
                participantIds = participants.map { it.id },
                classes = classes.map { it.name },
                criteriaIds = criteria.map { it.id },
                scores = scores.buildWith(participants, criteria),
            )
        }
    }

    fun toFirebaseActivity(value: NewActivity): FirebaseActivity {
        return with(value) {
            FirebaseActivity(
                name = name,
                date = date,
                participantIds = participants.map { it.id },
                classes = classes.map { it.name },
                criteriaIds = criteria.map { it.id },
                scores = scores.buildWith(participants, criteria),
            )
        }
    }

    private fun ParticipantScores.buildWith(
        participants: List<Participant>,
        criteria: List<ActivityCriteria>,
    ): ParticipantScores {
        return buildMap {
            participants.forEach { participant ->
                put(
                    participant.id,
                    buildMap { criteria.forEach { c -> put(c.id, this@buildWith[participant.id]?.get(c.id) ?: 0) } },
                )
            }
        }
    }
}