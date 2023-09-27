package com.andradel.pathfinders.features.participant.add

import com.andradel.pathfinders.model.ScoutClass
import com.andradel.pathfinders.validation.ValidationResult

data class AddEditParticipantState(
    val name: String,
    val email: String,
    val nameValidation: ValidationResult,
    val emailValidation: ValidationResult,
    val scoutClass: ScoutClass?,
    val isValid: Boolean,
    val participantResult: ParticipantResult?,
    val canDoInvestiture: Boolean,
)