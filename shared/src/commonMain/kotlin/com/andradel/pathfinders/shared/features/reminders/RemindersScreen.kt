package com.andradel.pathfinders.shared.features.reminders

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.age_years_old
import pathfinders.shared.generated.resources.call_to_contact
import pathfinders.shared.generated.resources.ic_call
import pathfinders.shared.generated.resources.last_seen_days
import pathfinders.shared.generated.resources.no_shows
import pathfinders.shared.generated.resources.past_birthdays
import pathfinders.shared.generated.resources.reminders_screen
import pathfinders.shared.generated.resources.todays_birthdays
import pathfinders.shared.generated.resources.upcoming_birthdays

@Composable
fun RemindersScreen(navigator: NavController, viewModel: RemindersViewModel = koinViewModel()) {
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(titleRes = Res.string.reminders_screen, onIconClick = navigator::navigateUp)
        },
        content = { padding ->
            val state by viewModel.state.collectAsState()
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                when (val s = state) {
                    is RemindersState.Loaded -> RemindersColumn(s, modifier = Modifier.fillMaxSize())
                    RemindersState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        },
    )
}

@Composable
private fun RemindersColumn(state: RemindersState.Loaded, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(horizontal = 16.dp)) {
        state.birthdays.today?.let { todaysBirthdays ->
            item {
                SectionHeader(stringResource(Res.string.todays_birthdays))
            }
            items(todaysBirthdays, key = { it.id }) { participant ->
                ParticipantBirthdayItem(participant)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        state.birthdays.upcoming?.let { birthdaySection(Res.string.upcoming_birthdays, it) }
        state.birthdays.past?.let { birthdaySection(Res.string.past_birthdays, it) }
        if (state.divider) {
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }
        state.noShows?.let { noShowsSection(it) }
    }
}

private fun LazyListScope.birthdaySection(sectionHeader: StringResource, birthdaySection: BirthdaySection) {
    item {
        SectionHeader(stringResource(sectionHeader))
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
        SectionHeader(stringResource(Res.string.no_shows), modifier = Modifier.padding(bottom = 8.dp))
    }
    items(noShowsReminders.noShows, key = { "noshow-${it.id}" }) { participant ->
        ParticipantNoShowItem(participant)
    }
}

@Composable
private fun ParticipantNoShowItem(participant: ParticipantNoShow, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = participant.name, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.last_seen_days, participant.daysSince.toString()),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (participant.contact != null) {
            IconButton(
                onClick = {
                    // TODO
                    // val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:${participant.contact}") }
                    // context.startActivity(intent)
                },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_call),
                    contentDescription = stringResource(Res.string.call_to_contact, participant.contact),
                )
            }
        }
    }
}

@Composable
private fun ParticipantBirthdayItem(participant: ParticipantBirthday, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = participant.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(
            text = pluralStringResource(Res.plurals.age_years_old, participant.age, participant.age),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun BirthdaySubSectionHeader(header: String, modifier: Modifier = Modifier) {
    Text(
        text = header,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier.padding(top = 12.dp, bottom = 4.dp),
    )
}

@Composable
private fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, modifier = modifier)
}