package com.andradel.pathfinders.model.activity

import androidx.annotation.Keep
import com.andradel.pathfinders.model.ScoutClass
import com.andradel.pathfinders.model.participant.Participant

@Keep
data class NewActivity(
    val name: String,
    val date: String,
    val participants: List<Participant>,
    val classes: List<ScoutClass>,
    val criteria: List<ActivityCriteria>,
    val scores: ParticipantScores,
)