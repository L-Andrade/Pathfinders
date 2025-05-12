package com.andradel.pathfinders.validation

import com.andradel.pathfinders.R
import javax.inject.Inject

class NameValidation @Inject constructor() {
    fun validate(name: String): ValidationResult {
        return when {
            name.isEmpty() -> ValidationResult.Invalid(R.string.name_hint)
            name.isBlank() || name.length < 2 -> ValidationResult.Invalid(R.string.name_invalid)
            else -> ValidationResult.Valid
        }
    }
}