package com.andradel.pathfinders.shared

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.andradel.pathfinders.shared.features.activity.add.AddEditActivityScreen
import com.andradel.pathfinders.shared.features.activity.add.criteria.AddCriteriaToActivityScreen
import com.andradel.pathfinders.shared.features.activity.add.participant.AddParticipantsToActivityScreen
import com.andradel.pathfinders.shared.features.activity.evaluate.individual.EvaluateActivityScreen
import com.andradel.pathfinders.shared.features.activity.evaluate.team.EvaluateTeamActivityScreen
import com.andradel.pathfinders.shared.features.activity.list.ActivityListScreen
import com.andradel.pathfinders.shared.features.admin.AdminScreen
import com.andradel.pathfinders.shared.features.admin.archive.ArchiveListScreen
import com.andradel.pathfinders.shared.features.admin.archive.create.CreateArchiveScreen
import com.andradel.pathfinders.shared.features.admin.archive.create.select.ArchiveSelectActivitiesManuallyScreen
import com.andradel.pathfinders.shared.features.admin.role.EditUserRoleScreen
import com.andradel.pathfinders.shared.features.admin.users.AdminUserListScreen
import com.andradel.pathfinders.shared.features.home.HomeScreen
import com.andradel.pathfinders.shared.features.participant.add.AddEditParticipantScreen
import com.andradel.pathfinders.shared.features.participant.list.ParticipantListScreen
import com.andradel.pathfinders.shared.features.participant.profile.ParticipantProfileScreen
import com.andradel.pathfinders.shared.features.reminders.RemindersScreen
import com.andradel.pathfinders.shared.features.team.add.AddEditTeamScreen
import com.andradel.pathfinders.shared.features.team.list.TeamListScreen
import com.andradel.pathfinders.shared.features.team.profile.TeamProfileScreen
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.Navigator
import com.andradel.pathfinders.shared.nav.rememberNavigationState
import com.andradel.pathfinders.shared.nav.rememberResultStore
import com.andradel.pathfinders.shared.ui.theme.PathfindersTheme

@Composable
fun Pathfinders(onSignInClick: () -> Unit) {
    PathfindersTheme {
        val navigationState = rememberNavigationState(startRoute = NavigationRoute.Home)
        val resultStore = rememberResultStore()
        val navigator = remember { Navigator(navigationState, resultStore) }
        NavDisplay(
            backStack = navigationState.backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            transitionSpec = {
                slideInHorizontally { size -> size } togetherWith slideOutHorizontally { size -> -size }
            },
            popTransitionSpec = {
                slideInHorizontally { size -> -size } togetherWith slideOutHorizontally { size -> size }
            },
            predictivePopTransitionSpec = {
                slideInHorizontally { size -> -size } togetherWith slideOutHorizontally { size -> size }
            },
            entryProvider = entryProvider {
                entry<NavigationRoute.Home> {
                    HomeScreen(onSignInClick = onSignInClick, navigator = navigator)
                }
                entry<NavigationRoute.ActivityList> { route ->
                    ActivityListScreen(route.archiveName, navigator = navigator)
                }
                entry<NavigationRoute.Admin> {
                    AdminScreen(navigator = navigator)
                }
                entry<NavigationRoute.ParticipantList> { route ->
                    ParticipantListScreen(route.archiveName, navigator = navigator)
                }
                entry<NavigationRoute.ParticipantProfile> { route ->
                    ParticipantProfileScreen(route.participant, route.archiveName, navigator = navigator)
                }
                entry<NavigationRoute.Reminders> {
                    RemindersScreen(navigator = navigator)
                }
                entry<NavigationRoute.AddEditActivity> { route ->
                    AddEditActivityScreen(route.activity, navigator = navigator)
                }
                entry<NavigationRoute.EvaluateActivity> { route ->
                    EvaluateActivityScreen(route.activity, navigator = navigator)
                }
                entry<NavigationRoute.AddCriteriaToActivity> { route ->
                    AddCriteriaToActivityScreen(route.selected, navigator = navigator)
                }
                entry<NavigationRoute.AddParticipantsToActivity> { route ->
                    AddParticipantsToActivityScreen(route.selected, navigator = navigator)
                }
                entry<NavigationRoute.AddEditParticipant> { route ->
                    AddEditParticipantScreen(route.participant, navigator = navigator)
                }
                entry<NavigationRoute.EditUserRole> { route ->
                    EditUserRoleScreen(route.user, navigator = navigator)
                }
                entry<NavigationRoute.AdminUserList> {
                    AdminUserListScreen(navigator = navigator)
                }
                entry<NavigationRoute.ArchiveList> {
                    ArchiveListScreen(navigator = navigator)
                }
                entry<NavigationRoute.CreateArchive> {
                    CreateArchiveScreen(navigator = navigator)
                }
                entry<NavigationRoute.ArchiveSelectActivitiesManually> { route ->
                    ArchiveSelectActivitiesManuallyScreen(route.activityIds, navigator = navigator)
                }
                entry<NavigationRoute.TeamList> { route ->
                    TeamListScreen(route.archiveName, navigator = navigator)
                }
                entry<NavigationRoute.AddEditTeam> { route ->
                    AddEditTeamScreen(route.team, route.archiveName, navigator = navigator)
                }
                entry<NavigationRoute.EvaluateTeamActivity> { route ->
                    EvaluateTeamActivityScreen(route.activity, navigator = navigator)
                }
                entry<NavigationRoute.TeamProfile> { route ->
                    TeamProfileScreen(route.team, route.archiveName, navigator = navigator)
                }
            },
        )
    }
}