package com.andradel.pathfinders

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.andradel.pathfinders.shared.Pathfinders
import com.andradel.pathfinders.shared.user.UserSession
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val userSession: UserSession by inject()

    private val signInResultLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        userSession.updateUser()
    }

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { _ ->
        // If not granted, we can show something informing that notifications won't be received
        // But for now, we won't show anything special
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> Unit

                shouldShowRequestPermissionRationale(POST_NOTIFICATIONS) -> {
                    // We should tell the user why we have notifications
                    // And ask the user if he wants to enable them with. If they accept, use:
                    // requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }

                else -> requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission()

        lifecycleScope.launch {
            userSession.setUserToken(Firebase.messaging.getToken())
        }
        setContent {
            Pathfinders(onSignInClick = { signInResultLauncher.launch(signInIntent) })
        }
    }

    companion object {
        private val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(
                listOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()),
            )
            .setTheme(R.style.Theme_Pathfinders)
            .build()
    }
}