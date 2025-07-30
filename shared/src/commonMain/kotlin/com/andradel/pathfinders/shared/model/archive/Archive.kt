package com.andradel.pathfinders.shared.model.archive

import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import com.andradel.pathfinders.shared.model.participant.Participant

data class Archive(
    val name: String,
    val activities: List<Activity>,
    val participants: List<Participant>,
    val criteria: List<ActivityCriteria>,
)