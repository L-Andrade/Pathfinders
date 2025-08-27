package com.andradel.pathfinders.shared.features.participant.add

import com.andradel.pathfinders.flavors.model.ParticipantClass
import com.andradel.pathfinders.shared.validation.ValidationResult

data class AddEditParticipantState(
    val name: String,
    val email: String,
    val contact: String,
    val birthdayRepresentation: String?,
    val birthday: Long,
    val nameValidation: ValidationResult,
    val emailValidation: ValidationResult,
    val contactValidation: ValidationResult,
    val participantClass: ParticipantClass?,
    val classOptions: List<ParticipantClass>,
    val isValid: Boolean,
    val participantResult: ParticipantResult?,
    val canDoInvestiture: Boolean,
)