package com.andradel.pathfinders

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import com.andradel.pathfinders.ui.theme.PathfindersTheme
import com.andradel.pathfinders.user.UserSession
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userSession: UserSession

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
            val result = Firebase.messaging.token.await()
            userSession.setUserToken(result)
        }
        setContent {
            PathfindersTheme {
                val animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold,
                )
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    defaultTransitions = remember {
                        object : NavHostAnimatedDestinationStyle() {
                            override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                                { slideInHorizontally(animationSpec) { size -> size } }
                            override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                                { slideOutHorizontally(animationSpec) { size -> -size } }
                            override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                                { slideInHorizontally(animationSpec) { size -> -size } }
                            override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                                { slideOutHorizontally(animationSpec) { size -> size } }

                        }
                    },
                    engine = rememberNavHostEngine(),
                    modifier = Modifier.imePadding(),
                )
            }
        }
    }
}