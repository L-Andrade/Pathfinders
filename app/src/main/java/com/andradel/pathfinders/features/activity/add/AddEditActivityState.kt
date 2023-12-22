package com.andradel.pathfinders.features.activity.add

import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.activity.ActivityCriteria
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.validation.ValidationResult
import java.time.LocalDate

data class AddEditActivityState(
    val name: String = "",
    val nameValidation: ValidationResult? = null,
    val date: LocalDate? = null,
    val classes: List<ParticipantClass> = emptyList(),
    val participants: List<Participant> = emptyList(),
    val criteria: List<ActivityCriteria> = emptyList(),
    val isValid: Boolean = false,
    val addActivityResult: AddActivityResult? = null
)