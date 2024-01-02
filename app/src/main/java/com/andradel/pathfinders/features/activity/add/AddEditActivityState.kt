package com.andradel.pathfinders.features.activity.add

import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.activity.ActivityCriteria
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.validation.ValidationResult

data class AddEditActivityState(
    val name: String = "",
    val nameValidation: ValidationResult? = null,
    val dateRepresentation: String? = null,
    val date: Long = 0L,
    val classes: List<ParticipantClass> = emptyList(),
    val participants: List<Participant> = emptyList(),
    val criteria: List<ActivityCriteria> = emptyList(),
    val isValid: Boolean = false,
    val isAdmin: Boolean = false,
    val activityResult: ActivityResult? = null
)