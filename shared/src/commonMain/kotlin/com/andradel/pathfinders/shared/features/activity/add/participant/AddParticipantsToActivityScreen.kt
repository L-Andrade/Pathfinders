package com.andradel.pathfinders.shared.features.activity.add.participant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.andradel.pathfinders.shared.model.ParticipantClass
import com.andradel.pathfinders.shared.model.color
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.model.title
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.navigateBackWithResult
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.activity_not_saved_description
import pathfinders.shared.generated.resources.activity_not_saved_title
import pathfinders.shared.generated.resources.add
import pathfinders.shared.generated.resources.filtering_by_class
import pathfinders.shared.generated.resources.ic_add
import pathfinders.shared.generated.resources.no_more_participants
import pathfinders.shared.generated.resources.participant_list
import pathfinders.shared.generated.resources.participant_selection
import pathfinders.shared.generated.resources.save
import pathfinders.shared.generated.resources.select_number_of_participants
import pathfinders.shared.generated.resources.select_participants_for_activity

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddParticipantsToActivityScreen(
    navigator: NavController,
    viewModel: AddParticipantsToActivityViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showUnsavedDialog by remember { mutableStateOf(false) }
    val onBack: () -> Unit = {
        if (viewModel.isUnsaved) {
            showUnsavedDialog = true
        } else {
            navigator.navigateUp()
        }
    }
    val onSelectParticipants: () -> Unit = {
        val participants = (state as? AddParticipantsToActivityState.Loaded)?.selection.orEmpty()
        navigator.navigateBackWithResult(NavigationRoute.AddParticipantsToActivity.Result, participants)
    }
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = Res.string.select_participants_for_activity,
                onIconClick = onBack,
                endContent = {
                    TextButton(onClick = onSelectParticipants) {
                        Text(text = stringResource(Res.string.save))
                    }
                },
            )
        },
    ) { padding ->
        BackHandler(onBack = onBack)
        if (showUnsavedDialog) {
            ConfirmationDialog(
                title = stringResource(Res.string.activity_not_saved_title),
                body = stringResource(Res.string.activity_not_saved_description),
                onDismiss = { showUnsavedDialog = false },
                navigator::navigateUp,
            )
        }
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            when (val s = state) {
                is AddParticipantsToActivityState.Loaded -> ParticipantSelectionList(
                    state = s,
                    onSelectParticipant = viewModel::selectParticipant,
                    onUnselectParticipant = viewModel::unselectParticipant,
                    onAddNewParticipant = { navigator.navigate(NavigationRoute.AddEditParticipant()) },
                    onSelectParticipants = onSelectParticipants,
                    onFilteringByClass = viewModel::setFilteringByClass,
                )
                AddParticipantsToActivityState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun ParticipantSelectionList(
    state: AddParticipantsToActivityState.Loaded,
    onSelectParticipant: (Participant) -> Unit,
    onUnselectParticipant: (Participant) -> Unit,
    onAddNewParticipant: () -> Unit,
    onSelectParticipants: () -> Unit,
    onFilteringByClass: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        if (state.selection.isNotEmpty()) {
            item {
                Header(header = Res.string.participant_selection, modifier = Modifier.padding(all = 16.dp))
            }
            items(state.selection, key = { it.id }) { p ->
                Participant(
                    participant = p,
                    selected = true,
                    onClick = { onUnselectParticipant(p) },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        }
        item {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
            ) {
                Header(header = Res.string.participant_list) {
                    OutlinedButton(onClick = onAddNewParticipant) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_add),
                                contentDescription = stringResource(Res.string.add),
                            )
                            Text(text = stringResource(Res.string.add))
                        }
                    }
                }
                FilterByClassSwitch(state.classes, state.filteringByClass, onFilteringByClass)
            }
        }
        if (state.participants.isEmpty()) {
            item {
                NoMoreParticipants(modifier = Modifier.padding(bottom = 16.dp))
            }
        } else {
            items(state.participants, key = { it.id }) { p ->
                Participant(
                    participant = p,
                    selected = false,
                    onClick = { onSelectParticipant(p) },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        }
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onSelectParticipants, modifier = Modifier.align(Alignment.Center)) {
                    Text(text = stringResource(Res.string.select_number_of_participants, state.selection.size))
                }
            }
        }
    }
}

@Composable
private fun FilterByClassSwitch(
    classes: List<ParticipantClass>,
    filteringByClass: Boolean,
    onFilteringByClass: (Boolean) -> Unit,
) {
    if (classes.isNotEmpty()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(
                    Res.string.filtering_by_class,
                    classes.map { it.title }.joinToString(),
                ),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            Switch(checked = filteringByClass, onCheckedChange = onFilteringByClass)
        }
    }
}

@Composable
private fun Participant(
    participant: Participant,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = participant.name,
            modifier = Modifier.weight(1f),
        )
        Checkbox(
            checked = selected, onCheckedChange = { onClick() },
            colors = CheckboxDefaults.colors(checkedColor = participant.participantClass.color),
        )
    }
}

@Composable
private fun NoMoreParticipants(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.no_more_participants),
        modifier = modifier.padding(horizontal = 16.dp),
    )
}

@Composable
private fun Header(header: StringResource, modifier: Modifier = Modifier, endContent: @Composable () -> Unit = {}) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(header),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        endContent()
    }
}