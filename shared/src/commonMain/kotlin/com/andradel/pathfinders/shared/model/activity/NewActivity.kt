package com.andradel.pathfinders.shared.model.activity

import com.andradel.pathfinders.flavors.model.ParticipantClass
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import com.andradel.pathfinders.shared.model.participant.Participant
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