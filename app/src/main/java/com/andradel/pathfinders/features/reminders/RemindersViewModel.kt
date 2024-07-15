package com.andradel.pathfinders.features.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.model.participant.Participant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    participantDataSource: ParticipantFirebaseDataSource,
    activityDataSource: ActivityFirebaseDataSource,
) : ViewModel() {
    private val dayMonthFormatter = DateTimeFormatter.ofPattern("dd MMMM")

    private val birthdays = participantDataSource.participants(null).map { participants ->
        val today = LocalDate.now()
        val interval = today.minusDays(7) to today.plusDays(14)
        val birthdays = participants.asSequence()
            .filter { participant ->
                val nextBirthday = participant.dateOfBirth?.nextBirthday(today, interval.first)
                nextBirthday != null && nextBirthday > interval.first && nextBirthday < interval.second
            }
            .sortedBy { it.dateOfBirth }
            .map {
                it.toParticipantBirthday(today, requireNotNull(it.dateOfBirth?.nextBirthday(today, interval.first)))
            }
        BirthdayReminders(
            today = birthdays.filter { it.state == BirthdayState.Today }.toList().takeIf { it.isNotEmpty() },
            upcoming = birthdays.toBirthdaySection(BirthdayState.Upcoming),
            past = birthdays.toBirthdaySection(BirthdayState.Past),
        )
    }

    private val noShows = combine(
        activityDataSource.activities(null), participantDataSource.participants(null),
    ) { activities, participants ->
        val today = LocalDate.now()
        val noShows = participants.mapNotNull { participant ->
            val lastUserActivity = activities.asSequence().filter { activity ->
                activity.date != null && activity.participants.any { it.id == participant.id }
            }.maxByOrNull { requireNotNull(it.date) }
            val last20Days = today.minusDays(20)
            if (lastUserActivity != null && requireNotNull(lastUserActivity.date) < last20Days) {
                val daysSince = ChronoUnit.DAYS.between(lastUserActivity.date, today)
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

    private fun LocalDate.nextBirthday(today: LocalDate, first: LocalDate): LocalDate {
        val thisYearsBirthday = withYear(today.year)
        return withYear(if (thisYearsBirthday < today && thisYearsBirthday < first) today.year + 1 else today.year)
    }

    private fun Sequence<ParticipantBirthday>.toBirthdaySection(state: BirthdayState): BirthdaySection? =
        BirthdaySection(filter { it.state == state }.toList().groupBy { it.date })
            .takeIf { it.birthdays.isNotEmpty() }

    private fun Participant.toParticipantBirthday(today: LocalDate, nextBirthday: LocalDate): ParticipantBirthday {
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