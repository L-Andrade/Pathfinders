package com.andradel.pathfinders.features.reminders

data class NoShowsReminders(
    val noShows: List<ParticipantNoShow>,
)

data class ParticipantNoShow(
    val id: String,
    val name: String,
    val contact: String?,
    val daysSince: Long,
)