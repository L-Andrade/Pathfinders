package com.andradel.pathfinders.features.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.model.participant.Participant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    participantDataSource: ParticipantFirebaseDataSource,
) : ViewModel() {
    private val formatter = DateTimeFormatter.ofPattern("dd MMMM")

    val birthdays = participantDataSource.participants.map { participants ->
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
        BirthdayRemindersState(
            today = birthdays.filter { it.state == BirthdayState.Today }.toList().takeIf { it.isNotEmpty() },
            upcoming = birthdays.toBirthdaySection(BirthdayState.Upcoming),
            past = birthdays.toBirthdaySection(BirthdayState.Past),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BirthdayRemindersState(today = null, upcoming = null, past = null)
    )

    val noShows = participantDataSource.participants.map { participants ->
        // participants.map { participant ->
        //     val activitiesForUser = activityDataSource.activitiesForUser(participant.id)
        // }
    }

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
            date = formatter.format(nextBirthday).uppercase(),
        )
    }
}