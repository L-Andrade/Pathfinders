package com.andradel.pathfinders.shared.validation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface ValidationResult {
    object Valid : ValidationResult
    data class Invalid(val message: StringResource) : ValidationResult
}

val ValidationResult.isValid: Boolean get() = this is ValidationResult.Valid
val ValidationResult.isError: Boolean get() = this is ValidationResult.Invalid

val ValidationResult.errorMessage: String?
    @Composable get() = if (this is ValidationResult.Invalid) stringResource(this.message) else null