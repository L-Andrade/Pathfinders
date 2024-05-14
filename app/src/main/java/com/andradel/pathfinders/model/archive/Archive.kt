package com.andradel.pathfinders.model.archive

import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.activity.ActivityCriteria
import com.andradel.pathfinders.model.participant.Participant

data class Archive(
    val name: String,
    val activities: List<Activity>,
    val participants: List<Participant>,
    val criteria: List<ActivityCriteria>
)