package com.andradel.pathfinders.shared.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAuthUiHandler(onResult: () -> Unit): AuthUiHandler {
    return remember { AuthUiHandler() }
}

actual class AuthUiHandler {
    actual fun onSignInClick() {
    }
}