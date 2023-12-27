package com.andradel.pathfinders.features.reminders

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andradel.pathfinders.R
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun RemindersScreen(
    navigator: DestinationsNavigator,
    viewModel: RemindersViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(titleRes = R.string.reminders_screen, onIconClick = navigator::navigateUp)
        },
        content = { padding ->
            val state by viewModel.state.collectAsState()
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when (val s = state) {
                    is RemindersState.Loaded -> RemindersColumn(s)
                    RemindersState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    )
}

@Composable
private fun RemindersColumn(state: RemindersState.Loaded, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(horizontal = 16.dp)) {
        state.birthdays.today?.let { todaysBirthdays ->
            item {
                SectionHeader(stringResource(id = R.string.todays_birthdays))
            }
            items(todaysBirthdays, key = { it.id }) { participant ->
                ParticipantBirthdayItem(participant)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        state.birthdays.upcoming?.let { birthdaySection(R.string.upcoming_birthdays, it) }
        state.birthdays.past?.let { birthdaySection(R.string.past_birthdays, it) }
        if (state.divider) {
            item {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }
        state.noShows?.let { noShowsSection(it) }
    }
}

private fun LazyListScope.birthdaySection(@StringRes sectionHeader: Int, birthdaySection: BirthdaySection) {
    item {
        SectionHeader(stringResource(id = sectionHeader))
    }
    birthdaySection.birthdays.forEach { (header, items) ->
        item {
            BirthdaySubSectionHeader(header)
        }
        items(items, key = { it.id }) { participant ->
            ParticipantBirthdayItem(participant)
        }
    }
    item {
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun LazyListScope.noShowsSection(noShowsReminders: NoShowsReminders) {
    item {
        SectionHeader(stringResource(id = R.string.no_shows), modifier = Modifier.padding(bottom = 8.dp))
    }
    items(noShowsReminders.noShows, key = { "noshow-${it.id}" }) { participant ->
        ParticipantNoShowItem(participant)
    }
}

@Composable
private fun ParticipantNoShowItem(participant: ParticipantNoShow, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Text(text = participant.name, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.last_seen_days, participant.daysSince),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ParticipantBirthdayItem(participant: ParticipantBirthday, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = participant.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(
            text = pluralStringResource(id = R.plurals.age_years_old, participant.age, participant.age),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun BirthdaySubSectionHeader(header: String, modifier: Modifier = Modifier) {
    Text(
        text = header,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, modifier = modifier)
}