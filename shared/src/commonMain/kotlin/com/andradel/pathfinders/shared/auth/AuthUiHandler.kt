package com.andradel.pathfinders.shared.auth

import androidx.compose.runtime.Composable

@Composable
expect fun rememberAuthUiHandler(onResult: () -> Unit): AuthUiHandler

expect class AuthUiHandler {
    fun onSignInClick()
}