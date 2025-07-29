package com.andradel.pathfinders.features.reminders

sealed interface RemindersState {
    data object Loading : RemindersState
    data class Loaded(
        val birthdays: BirthdayReminders,
        val noShows: NoShowsReminders?,
        val divider: Boolean,
    ) : RemindersState
}