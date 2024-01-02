package com.andradel.pathfinders

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.IntOffset
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.lifecycleScope
import com.andradel.pathfinders.features.NavGraphs
import com.andradel.pathfinders.ui.theme.PathfindersTheme
import com.andradel.pathfinders.user.UserSession
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var uesrSession: UserSession

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // TODO: only do it for admin/class admin
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    // FCM SDK (and your app) can post notifications.
                }

                shouldShowRequestPermissionRationale(POST_NOTIFICATIONS) -> {
                    // TODO: display UI asking for notification.
                    //  If accepted, show it with requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }

                else -> requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        askNotificationPermission()

        lifecycleScope.launch {
            val result = Firebase.messaging.token.await()
            uesrSession.setUserToken(result)
        }
        setContent {
            PathfindersTheme {
                val animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    engine = rememberAnimatedNavHostEngine(
                        rootDefaultAnimations = RootNavGraphDefaultAnimations(
                            enterTransition = { slideInHorizontally(animationSpec) { size -> size } },
                            exitTransition = { slideOutHorizontally(animationSpec) { size -> -size } },
                            popEnterTransition = { slideInHorizontally(animationSpec) { size -> -size } },
                            popExitTransition = { slideOutHorizontally(animationSpec) { size -> size } },
                        )
                    )
                )
            }
        }
    }
}