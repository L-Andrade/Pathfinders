package com.andradel.pathfinders.shared.features.admin.archive.create.select

import com.andradel.pathfinders.shared.model.activity.Activity
import kotlinx.serialization.Serializable

@Serializable
data class SelectedActivities(val selected: List<Activity>)