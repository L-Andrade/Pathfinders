package com.andradel.pathfinders.features.reminders

data class BirthdayRemindersState(
    val today: List<ParticipantBirthday>?,
    val upcoming: BirthdaySection?,
    val past: BirthdaySection?,
)

data class BirthdaySection(val birthdays: Map<String, List<ParticipantBirthday>>)

data class ParticipantBirthday(
    val id: String,
    val name: String,
    val age: Int,
    val state: BirthdayState,
    val date: String,
)

enum class BirthdayState {
    Today,
    Past,
    Upcoming,
}