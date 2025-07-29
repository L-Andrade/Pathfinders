package com.andradel.pathfinders.model.activity

import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.criteria.ActivityCriteria
import com.andradel.pathfinders.model.participant.Participant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    val id: String,
    val name: String,
    val date: LocalDate?,
    val participants: List<Participant>,
    val classes: List<ParticipantClass>,
    val criteria: List<ActivityCriteria>,
    val scores: ParticipantScores,
    val archiveName: String?,
)

fun Activity.participantPoints(participantId: String): Int {
    return scores[participantId]?.values?.sum() ?: 0
}