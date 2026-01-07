package com.andradel.pathfinders.shared.features.team.profile

sealed interface TeamProfileState {
    val title: String

    data class Loading(override val title: String) : TeamProfileState
    data class Loaded(
        override val title: String,
        val teamParticipants: List<TeamParticipant>,
        val teamActivities: List<TeamActivity>,
        val points: Int,
    ) : TeamProfileState
}

data class TeamParticipant(
    val id: String,
    val name: String,
    val points: Int,
    val activities: List<TeamParticipantActivity>,
)

data class TeamParticipantActivity(
    val id: String,
    val name: String,
    val points: Int,
)

data class TeamActivity(
    val id: String,
    val name: String,
    val date: String?,
    val participants: List<TeamActivityParticipant>,
    val total: Int,
)

data class TeamActivityParticipant(
    val id: String,
    val name: String?,
    val points: Int,
)