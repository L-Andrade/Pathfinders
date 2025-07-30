package com.andradel.pathfinders.shared.validation

import org.koin.core.annotation.Factory
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.name_hint
import pathfinders.shared.generated.resources.name_invalid

@Factory
class NameValidation {
    fun validate(name: String): ValidationResult {
        return when {
            name.isEmpty() -> ValidationResult.Invalid(Res.string.name_hint)
            name.isBlank() || name.length < 2 -> ValidationResult.Invalid(Res.string.name_invalid)
            else -> ValidationResult.Valid
        }
    }
}