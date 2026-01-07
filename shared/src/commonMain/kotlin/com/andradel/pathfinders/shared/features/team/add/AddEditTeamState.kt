package com.andradel.pathfinders.shared.features.team.add

import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.validation.ValidationResult

data class AddEditTeamState(
    val name: String = "",
    val nameValidation: ValidationResult = ValidationResult.Valid,
    val participants: List<Participant> = emptyList(),
    val isValid: Boolean = false,
    val teamResult: TeamResult? = null,
)