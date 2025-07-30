package com.andradel.pathfinders.shared.validation

import org.koin.core.annotation.Factory
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.email_invalid

@Factory
class EmailValidation {
    private val regex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()

    fun validate(email: String, optional: Boolean = true): ValidationResult {
        return when {
            optional && email.isEmpty() -> ValidationResult.Valid
            !regex.matches(email) -> ValidationResult.Invalid(Res.string.email_invalid)
            else -> ValidationResult.Valid
        }
    }
}