package com.andradel.pathfinders.validation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface ValidationResult {
    object Valid : ValidationResult
    data class Invalid(@StringRes val message: Int) : ValidationResult
}

val ValidationResult.isValid: Boolean get() = this is ValidationResult.Valid
val ValidationResult.isError: Boolean get() = this is ValidationResult.Invalid

val ValidationResult.errorMessage: String?
    @Composable get() = if (this is ValidationResult.Invalid) stringResource(id = this.message) else null