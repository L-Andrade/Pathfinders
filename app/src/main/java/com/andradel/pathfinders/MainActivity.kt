package com.andradel.pathfinders

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andradel.pathfinders.features.activity.add.AddEditActivityScreen
import com.andradel.pathfinders.features.activity.add.criteria.AddCriteriaToActivityScreen
import com.andradel.pathfinders.features.activity.add.participant.AddParticipantsToActivityScreen
import com.andradel.pathfinders.features.activity.evaluate.EvaluateActivityScreen
import com.andradel.pathfinders.features.activity.list.ActivityListScreen
import com.andradel.pathfinders.features.admin.AdminScreen
import com.andradel.pathfinders.features.admin.archive.ArchiveListScreen
import com.andradel.pathfinders.features.admin.archive.create.CreateArchiveScreen
import com.andradel.pathfinders.features.admin.archive.create.select.ArchiveSelectActivitiesManuallyScreen
import com.andradel.pathfinders.features.admin.role.EditUserRoleScreen
import com.andradel.pathfinders.features.admin.users.AdminUserListScreen
import com.andradel.pathfinders.features.home.HomeScreen
import com.andradel.pathfinders.features.participant.add.AddEditParticipantScreen
import com.andradel.pathfinders.features.participant.list.ParticipantListScreen
import com.andradel.pathfinders.features.participant.profile.ParticipantProfileScreen
import com.andradel.pathfinders.features.reminders.RemindersScreen
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.criteria.ActivityCriteria
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.nav.NavigationRoute
import com.andradel.pathfinders.nav.customNavType
import com.andradel.pathfinders.ui.theme.PathfindersTheme
import com.andradel.pathfinders.user.User
import com.andradel.pathfinders.user.UserSession
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {
    private val userSession: UserSession by inject()

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

    @OptIn(ExperimentalAnimationApi::class)
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
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NavigationRoute.Home,
                    enterTransition = { slideInHorizontally { size -> size } },
                    exitTransition = { slideOutHorizontally { size -> -size } },
                    popEnterTransition = { slideInHorizontally { size -> -size } },
                    popExitTransition = { slideOutHorizontally { size -> size } },
                ) {
                    composable<NavigationRoute.Home> {
                        HomeScreen(navigator = navController)
                    }
                    composable<NavigationRoute.ActivityList> {
                        ActivityListScreen(navigator = navController)
                    }
                    composable<NavigationRoute.Admin> {
                        AdminScreen(navigator = navController)
                    }
                    composable<NavigationRoute.ParticipantList> {
                        ParticipantListScreen(navigator = navController)
                    }
                    composable<NavigationRoute.ParticipantProfile>(
                        typeMap = mapOf(typeOf<Participant>() to customNavType<Participant>()),
                    ) {
                        ParticipantProfileScreen(navigator = navController)
                    }
                    composable<NavigationRoute.Reminders> {
                        RemindersScreen(navigator = navController)
                    }
                    composable<NavigationRoute.AddEditActivity>(
                        typeMap = mapOf(typeOf<Activity?>() to customNavType<Activity?>(isNullableAllowed = true)),
                    ) {
                        AddEditActivityScreen(navigator = navController)
                    }
                    composable<NavigationRoute.EvaluateActivity>(
                        typeMap = mapOf(typeOf<Activity>() to customNavType<Activity>()),
                    ) {
                        EvaluateActivityScreen(navigator = navController)
                    }
                    composable<NavigationRoute.AddCriteriaToActivity>(
                        typeMap = mapOf(typeOf<List<ActivityCriteria>>() to customNavType<List<ActivityCriteria>>())
                    ) {
                        AddCriteriaToActivityScreen(navigator = navController)
                    }
                    composable<NavigationRoute.AddParticipantsToActivity>(
                        typeMap = mapOf(typeOf<List<Participant>>() to customNavType<List<Participant>>())
                    ) {
                        AddParticipantsToActivityScreen(navigator = navController)
                    }
                    composable<NavigationRoute.AddEditParticipant>(
                        typeMap = mapOf(typeOf<Participant?>() to customNavType<Participant?>(isNullableAllowed = true)),
                    ) {
                        AddEditParticipantScreen(navigator = navController)
                    }
                    composable<NavigationRoute.EditUserRole>(typeMap = mapOf(typeOf<User>() to customNavType<User>())) {
                        EditUserRoleScreen(navigator = navController)
                    }
                    composable<NavigationRoute.AdminUserList> {
                        AdminUserListScreen(navigator = navController)
                    }
                    composable<NavigationRoute.ArchiveList> {
                        ArchiveListScreen(navigator = navController)
                    }
                    composable<NavigationRoute.CreateArchive> {
                        CreateArchiveScreen(navigator = navController)
                    }
                    composable<NavigationRoute.ArchiveSelectActivitiesManually>(
                        typeMap = mapOf(typeOf<List<Activity>>() to customNavType<List<Activity>>()),
                    ) {
                        ArchiveSelectActivitiesManuallyScreen(navigator = navController)
                    }
                }
            }
        }
    }
}