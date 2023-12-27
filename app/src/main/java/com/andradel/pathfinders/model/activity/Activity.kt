package com.andradel.pathfinders.model.activity

import android.os.Parcelable
import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.participant.Participant
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Activity(
    val id: String,
    val name: String,
    val date: LocalDate?,
    val participants: List<Participant>,
    val classes: List<ParticipantClass>,
    val criteria: List<ActivityCriteria>,
    val scores: ParticipantScores,
) : Parcelable
fun Activity.participantPoints(participantId: String): Int {
    return scores[participantId]?.values?.sum() ?: 0
}