package com.andradel.pathfinders.shared.auth

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

@Composable
actual fun rememberAuthUiHandler(onResult: () -> Unit): AuthUiHandler {
    return AuthUiHandler(
        rememberLauncherForActivityResult(
            contract = FirebaseAuthUIActivityResultContract(),
            onResult = { onResult() },
        ),
    )
}

actual class AuthUiHandler(
    private val resultLauncher: ManagedActivityResultLauncher<Intent, FirebaseAuthUIAuthenticationResult>,
) {
    private val signInIntent = run {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            // TODO .setTheme(R.style.Theme_Pathfinders)
            .build()
    }

    actual fun onSignInClick() {
        resultLauncher.launch(signInIntent)
    }
}