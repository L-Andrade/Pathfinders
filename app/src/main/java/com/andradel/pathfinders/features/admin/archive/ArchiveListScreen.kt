package com.andradel.pathfinders.features.admin.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andradel.pathfinders.R
import com.andradel.pathfinders.features.destinations.ActivityListScreenDestination
import com.andradel.pathfinders.features.destinations.CreateArchiveScreenDestination
import com.andradel.pathfinders.features.destinations.ParticipantListScreenDestination
import com.andradel.pathfinders.ui.ConfirmationDialog
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination
fun ArchiveListScreen(navigator: DestinationsNavigator, viewModel: ArchiveListViewModel = koinViewModel()) {
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(titleRes = R.string.admin_archive, onIconClick = navigator::navigateUp)
        },
        content = { padding ->
            val state by viewModel.state.collectAsStateWithLifecycle()
            when (val s = state) {
                is ArchiveListState.Archives ->
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        item {
                            CreateArchive(onClick = { navigator.navigate(CreateArchiveScreenDestination) })
                        }
                        items(s.archives, key = { it.name }) { item ->
                            ArchiveItem(
                                item = item,
                                onActivitiesClick = { navigator.navigate(ActivityListScreenDestination(item.name)) },
                                onParticipantsClick = {
                                    navigator.navigate(ParticipantListScreenDestination(item.name))
                                },
                                onDelete = { viewModel.onDeleteArchive(item.name) },
                            )
                        }
                    }

                ArchiveListState.Loading -> Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        },
    )
}

@Composable
private fun CreateArchive(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Card(onClick = onClick) {
            Row(modifier = Modifier.padding(all = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.create_archive),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Icon(painter = painterResource(id = R.drawable.ic_chevron_right), contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
    }
}

@Composable
private fun ArchiveItem(
    item: ArchiveItem,
    onParticipantsClick: () -> Unit,
    onActivitiesClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(all = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                DeleteButton(item.name, onDelete)
            }
            if (item.startDate != null && item.endDate != null) {
                Dates(item.startDate, item.endDate)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onActivitiesClick, modifier = Modifier.weight(1f)) {
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.archive_activities, item.activities, item.activities,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Button(onClick = onParticipantsClick, modifier = Modifier.weight(1f)) {
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.archive_participants, item.participants, item.participants,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteButton(name: String, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    var showConfirmation by remember { mutableStateOf(false) }
    if (showConfirmation) {
        ConfirmationDialog(
            title = stringResource(id = R.string.delete),
            body = stringResource(id = R.string.delete_confirmation, name),
            onDismiss = { showConfirmation = false },
            onConfirm = {
                onDelete()
                showConfirmation = false
            },
        )
    }
    IconButton(onClick = { showConfirmation = true }, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = stringResource(id = R.string.delete),
        )
    }
}

@Composable
private fun Dates(startDate: String, endDate: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_calendar),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(text = startDate, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 4.dp))
        Icon(
            Icons.AutoMirrored.Default.ArrowForward,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(horizontal = 4.dp),
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_calendar),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(text = endDate, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 4.dp))
    }
}