package com.andradel.pathfinders.validation

import com.andradel.pathfinders.R
import javax.inject.Inject

class EmailValidation @Inject constructor() {
    private val regex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()

    fun validate(email: String, optional: Boolean = true): ValidationResult {
        return when {
            optional && email.isEmpty() -> ValidationResult.Valid
            !regex.matches(email) -> ValidationResult.Invalid(R.string.email_invalid)
            else -> ValidationResult.Valid
        }
    }
}