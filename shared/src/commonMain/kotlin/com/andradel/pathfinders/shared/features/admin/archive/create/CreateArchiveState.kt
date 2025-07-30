package com.andradel.pathfinders.shared.features.admin.archive.create

import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.validation.ValidationResult

data class CreateArchiveState(
    val name: String = "",
    val nameValidation: ValidationResult? = null,
    val deleteParticipants: Boolean = false,
    val deleteCriteria: Boolean = false,
    val activities: List<Activity> = emptyList(),
    val participants: Affected? = null,
    val criteria: Affected? = null,
    val canSave: Boolean = false,
)

data class Affected(val size: Int, val usedInOtherActivities: Int)