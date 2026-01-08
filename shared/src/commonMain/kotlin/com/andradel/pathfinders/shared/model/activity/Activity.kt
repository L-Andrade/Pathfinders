package com.andradel.pathfinders.shared.model.activity

import com.andradel.pathfinders.flavors.model.ParticipantClass
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import com.andradel.pathfinders.shared.model.participant.Participant
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
    val teamScores: TeamScores,
    val archiveName: String?,
)

fun Activity.participantPoints(participantId: String): Int {
    return scores[participantId]?.values?.sum() ?: 0
}

fun Activity.teamPoints(teamId: String): Int {
    return teamScores[teamId]?.values?.flatMap { it.values }?.sum() ?: 0
}

fun Activity.teamPointsForParticipant(teamId: String, participantId: String): Int {
    return teamScores[teamId]?.get(participantId)?.values?.sum() ?: 0
}