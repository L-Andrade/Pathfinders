package com.andradel.pathfinders.nav

import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.criteria.ActivityCriteria
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.user.User
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
    data class AddEditActivity(val activity: Activity? = null) : NavigationRoute

    @Serializable
    data class EvaluateActivity(val activity: Activity) : NavigationRoute

    @Serializable
    data class AddCriteriaToActivity(val criteria: List<ActivityCriteria>) : NavigationRoute {
        companion object {
            const val Result = "AddCriteriaToActivityResult"
        }
    }

    @Serializable
    data class AddParticipantsToActivity(
        val participants: List<Participant>,
        val classes: List<ParticipantClass>,
    ) : NavigationRoute {
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
    data class ArchiveSelectActivitiesManually(val activities: List<Activity>) : NavigationRoute {
        companion object {
            const val Result = "ArchiveSelectActivitiesManuallyResult"
        }
    }
}