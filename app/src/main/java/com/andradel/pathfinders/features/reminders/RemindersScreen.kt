package com.andradel.pathfinders.features.reminders

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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
            val birthdays by viewModel.birthdays.collectAsState()
            LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 16.dp)) {
                birthdays.today?.let { todaysBirthdays ->
                    item {
                        BirthdaySectionHeader(stringResource(id = R.string.todays_birthdays))
                    }
                    items(todaysBirthdays, key = { it.id }) { participant ->
                        ParticipantBirthdayItem(participant)
                    }
                }
                birthdays.upcoming?.let { birthdaySection(R.string.upcoming_birthdays, it) }
                birthdays.past?.let { birthdaySection(R.string.past_birthdays, it) }
            }
        }
    )
}

private fun LazyListScope.birthdaySection(@StringRes sectionHeader: Int, birthdaySection: BirthdaySection) {
    item {
        BirthdaySectionHeader(stringResource(id = sectionHeader))
    }
    birthdaySection.birthdays.forEach { (header, items) ->
        item {
            BirthdaySubSectionHeader(header)
        }
        items(items, key = { it.id }) { participant ->
            ParticipantBirthdayItem(participant)
        }
    }
}

@Composable
private fun ParticipantBirthdayItem(participant: ParticipantBirthday, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = participant.name, modifier = Modifier.weight(1f))
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
private fun BirthdaySectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, modifier = modifier.padding(top = 16.dp))
}