package com.andradel.pathfinders.shared.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(NavigationRoute.Home::class, NavigationRoute.Home.serializer())
            subclass(NavigationRoute.ActivityList::class, NavigationRoute.ActivityList.serializer())
            subclass(NavigationRoute.Admin::class, NavigationRoute.Admin.serializer())
            subclass(NavigationRoute.ParticipantList::class, NavigationRoute.ParticipantList.serializer())
            subclass(NavigationRoute.ParticipantProfile::class, NavigationRoute.ParticipantProfile.serializer())
            subclass(NavigationRoute.Reminders::class, NavigationRoute.Reminders.serializer())
            subclass(NavigationRoute.AddEditActivity::class, NavigationRoute.AddEditActivity.serializer())
            subclass(NavigationRoute.EvaluateActivity::class, NavigationRoute.EvaluateActivity.serializer())
            subclass(NavigationRoute.AddCriteriaToActivity::class, NavigationRoute.AddCriteriaToActivity.serializer())
            subclass(
                NavigationRoute.AddParticipantsToActivity::class,
                NavigationRoute.AddParticipantsToActivity.serializer(),
            )
            subclass(NavigationRoute.AddEditParticipant::class, NavigationRoute.AddEditParticipant.serializer())
            subclass(NavigationRoute.EditUserRole::class, NavigationRoute.EditUserRole.serializer())
            subclass(NavigationRoute.AdminUserList::class, NavigationRoute.AdminUserList.serializer())
            subclass(NavigationRoute.ArchiveList::class, NavigationRoute.ArchiveList.serializer())
            subclass(NavigationRoute.CreateArchive::class, NavigationRoute.CreateArchive.serializer())
            subclass(
                NavigationRoute.ArchiveSelectActivitiesManually::class,
                NavigationRoute.ArchiveSelectActivitiesManually.serializer(),
            )
            subclass(NavigationRoute.TeamList::class, NavigationRoute.TeamList.serializer())
            subclass(NavigationRoute.AddEditTeam::class, NavigationRoute.AddEditTeam.serializer())
            subclass(NavigationRoute.EvaluateTeamActivity::class, NavigationRoute.EvaluateTeamActivity.serializer())
            subclass(NavigationRoute.TeamProfile::class, NavigationRoute.TeamProfile.serializer())
        }
    }
}

@Composable
fun rememberNavigationState(startRoute: NavKey): NavigationState {
    val backStack = rememberNavBackStack(config, NavigationRoute.Home)
    return remember(startRoute) { NavigationState(backStack = backStack) }
}

class NavigationState(val backStack: NavBackStack<NavKey>)