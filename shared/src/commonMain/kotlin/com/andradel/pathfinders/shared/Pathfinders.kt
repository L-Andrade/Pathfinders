package com.andradel.pathfinders.shared

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andradel.pathfinders.shared.features.activity.add.AddEditActivityScreen
import com.andradel.pathfinders.shared.features.activity.add.criteria.AddCriteriaToActivityScreen
import com.andradel.pathfinders.shared.features.activity.add.participant.AddParticipantsToActivityScreen
import com.andradel.pathfinders.shared.features.activity.evaluate.EvaluateActivityScreen
import com.andradel.pathfinders.shared.features.activity.list.ActivityListScreen
import com.andradel.pathfinders.shared.features.admin.AdminScreen
import com.andradel.pathfinders.shared.features.admin.archive.ArchiveListScreen
import com.andradel.pathfinders.shared.features.admin.archive.create.CreateArchiveScreen
import com.andradel.pathfinders.shared.features.admin.role.EditUserRoleScreen
import com.andradel.pathfinders.shared.features.admin.users.AdminUserListScreen
import com.andradel.pathfinders.shared.features.home.HomeScreen
import com.andradel.pathfinders.shared.features.participant.add.AddEditParticipantScreen
import com.andradel.pathfinders.shared.features.participant.list.ParticipantListScreen
import com.andradel.pathfinders.shared.features.participant.profile.ParticipantProfileScreen
import com.andradel.pathfinders.shared.features.reminders.RemindersScreen
import com.andradel.pathfinders.shared.model.ParticipantClass
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.customNavType
import com.andradel.pathfinders.shared.ui.theme.PathfindersTheme
import com.andradel.pathfinders.shared.user.User
import kotlin.reflect.typeOf

@Composable
fun Pathfinders(
    onSignInClick: () -> Unit,
) {
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
                HomeScreen(onSignInClick = onSignInClick, navigator = navController)
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
                typeMap = mapOf(typeOf<List<ActivityCriteria>>() to customNavType<List<ActivityCriteria>>()),
            ) {
                AddCriteriaToActivityScreen(navigator = navController)
            }
            composable<NavigationRoute.AddParticipantsToActivity>(
                typeMap = mapOf(
                    typeOf<List<Participant>>() to customNavType<List<Participant>>(),
                    typeOf<List<ParticipantClass>>() to customNavType<List<ParticipantClass>>()
                ),
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
            // composable<NavigationRoute.ArchiveSelectActivitiesManually>(
            //     typeMap = mapOf(typeOf<List<Activity>>() to customNavType<List<Activity>>()),
            // ) {
            //     ArchiveSelectActivitiesManuallyScreen(navigator = navController)
            // }
        }
    }
}