package com.andradel.pathfinders.shared.nav

import com.andradel.pathfinders.shared.features.activity.add.criteria.SelectedCriteria
import com.andradel.pathfinders.shared.features.activity.add.participant.SelectedParticipants
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.user.User
import kotlinx.serialization.Serializable

sealed interface NavigationRoute {

    @Serializable
    data object Home : NavigationRoute

    @Serializable
    data class ActivityList(val archiveName: String? = null) : NavigationRoute

    @Serializable
    data object Admin : NavigationRoute

    @Serializable
    data class ParticipantList(val archiveName: String? = null) : NavigationRoute

    @Serializable
    data class ParticipantProfile(val participant: Participant, val archiveName: String? = null) : NavigationRoute

    @Serializable
    data object Reminders : NavigationRoute

    @Serializable
    data class AddEditActivity(val activityId: String? = null, val archiveName: String? = null) : NavigationRoute

    @Serializable
    data class EvaluateActivity(val activity: Activity) : NavigationRoute

    @Serializable
    data class AddCriteriaToActivity(val selected: SelectedCriteria) : NavigationRoute {
        companion object {
            const val Result = "AddCriteriaToActivityResult"
        }
    }

    @Serializable
    data class AddParticipantsToActivity(val selected: SelectedParticipants) : NavigationRoute {
        companion object {
            const val Result = "AddParticipantsToActivityResult"
        }
    }

    @Serializable
    data class AddEditParticipant(val participant: Participant? = null) : NavigationRoute

    @Serializable
    data class EditUserRole(val user: User) : NavigationRoute

    @Serializable
    data object AdminUserList : NavigationRoute

    @Serializable
    data object ArchiveList : NavigationRoute

    @Serializable
    data object CreateArchive : NavigationRoute

    @Serializable
    data class ArchiveSelectActivitiesManually(val activityIds: List<String>) : NavigationRoute {
        companion object {
            const val Result = "ArchiveSelectActivitiesManuallyResult"
        }
    }
}