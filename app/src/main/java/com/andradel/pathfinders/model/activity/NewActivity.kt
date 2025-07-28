package com.andradel.pathfinders.model.activity

import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.criteria.ActivityCriteria
import com.andradel.pathfinders.model.participant.Participant
import kotlinx.serialization.Serializable

@Serializable
data class NewActivity(
    val name: String,
    val date: String?,
    val participants: List<Participant>,
    val classes: List<ParticipantClass>,
    val criteria: List<ActivityCriteria>,
    val scores: ParticipantScores,
)