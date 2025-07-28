package com.andradel.pathfinders.features.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.model.participant.Participant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@KoinViewModel
class RemindersViewModel(
    participantDataSource: ParticipantFirebaseDataSource,
    activityDataSource: ActivityFirebaseDataSource,
) : ViewModel() {
    private val now = Clock.System.now()
    private val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val birthdayInterval = run {
        val sevenDaysAgo = now.minus(7.days).toLocalDateTime(TimeZone.currentSystemDefault()).date
        val fourteenDaysAfter = now.plus(14.days).toLocalDateTime(TimeZone.currentSystemDefault()).date
        sevenDaysAgo .. fourteenDaysAfter
    }
    private val lastActivityInterval = run {
        val twentyDaysAgo = now.plus(20.days).toLocalDateTime(TimeZone.currentSystemDefault()).date
        twentyDaysAgo..today
    }

    private val dayMonthFormatter = LocalDate.Format {
        day()
        char(' ')
        monthName(MonthNames.ENGLISH_FULL)
    }

    private val birthdays = participantDataSource.participants(null).map { participants ->
        val birthdays = participants.asSequence()
            .filter { participant ->
                val nextBirthday = participant.dateOfBirth?.birthdayAtStartOrNext()
                nextBirthday != null && nextBirthday in birthdayInterval
            }
            .sortedBy { it.dateOfBirth }
            .map { it.toParticipantBirthday(requireNotNull(it.dateOfBirth?.birthdayAtStartOrNext())) }
        BirthdayReminders(
            today = birthdays.filter { it.state == BirthdayState.Today }.toList().takeIf { it.isNotEmpty() },
            upcoming = birthdays.toBirthdaySection(BirthdayState.Upcoming),
            past = birthdays.toBirthdaySection(BirthdayState.Past),
        )
    }

    private val noShows = combine(
        activityDataSource.activities(null), participantDataSource.participants(null),
    ) { activities, participants ->
        val noShows = participants.mapNotNull { participant ->
            val lastUserActivity = activities.asSequence().filter { activity ->
                activity.date != null && activity.participants.any { it.id == participant.id }
            }.maxByOrNull { requireNotNull(it.date) }
            if (lastUserActivity?.date != null && lastUserActivity.date !in lastActivityInterval) {
                val daysSince = lastUserActivity.date.daysUntil(today)
                ParticipantNoShow(participant.id, participant.name, participant.contact, daysSince)
            } else {
                null
            }
        }.sortedBy { it.daysSince }
        NoShowsReminders(noShows = noShows).takeIf { noShows.isNotEmpty() }
    }

    val state = combine(birthdays, noShows) { birthdays, noShows ->
        val hasBirthdays = birthdays.past != null || birthdays.upcoming != null || birthdays.today != null
        RemindersState.Loaded(birthdays = birthdays, noShows = noShows, divider = hasBirthdays && noShows != null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RemindersState.Loading)

    private fun LocalDate.birthdayAtStartOrNext(): LocalDate {
        val birthdayThisYear = LocalDate(today.year, month, day)
        return if (birthdayThisYear < today && birthdayThisYear < birthdayInterval.start) {
            LocalDate(today.year + 1, month, day)
        } else {
            LocalDate(today.year, month, day)
        }
    }

    private fun Sequence<ParticipantBirthday>.toBirthdaySection(state: BirthdayState): BirthdaySection? =
        BirthdaySection(filter { it.state == state }.toList().groupBy { it.date })
            .takeIf { it.birthdays.isNotEmpty() }

    private fun Participant.toParticipantBirthday(nextBirthday: LocalDate): ParticipantBirthday {
        return ParticipantBirthday(
            id = id,
            name = name,
            age = (today.year - requireNotNull(dateOfBirth).year).coerceAtLeast(1),
            state = when {
                nextBirthday == today -> BirthdayState.Today
                nextBirthday < today -> BirthdayState.Past
                else -> BirthdayState.Upcoming
            },
            date = dayMonthFormatter.format(nextBirthday).uppercase(),
        )
    }
}