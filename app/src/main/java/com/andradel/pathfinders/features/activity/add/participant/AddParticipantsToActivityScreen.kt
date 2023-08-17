package com.andradel.pathfinders.features.activity.add.participant

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andradel.pathfinders.R
import com.andradel.pathfinders.features.destinations.AddEditParticipantScreenDestination
import com.andradel.pathfinders.model.ScoutClass
import com.andradel.pathfinders.model.activity.ParticipantSelectionArg
import com.andradel.pathfinders.model.color
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.model.title
import com.andradel.pathfinders.ui.ConfirmationDialog
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Composable
@Destination(navArgsDelegate = ParticipantSelectionArg::class)
fun AddParticipantsToActivityScreen(
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<ParticipantSelectionArg>,
    viewModel: AddParticipantsToActivityViewModel = hiltViewModel()
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
        resultNavigator.setResult(ParticipantSelectionArg(ArrayList(participants)))
        resultNavigator.navigateBack()
    }
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = R.string.select_participants_for_activity,
                onIconClick = onBack,
                endContent = {
                    TextButton(onClick = onSelectParticipants) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
            )
        }
    ) { padding ->
        BackHandler(onBack = onBack)
        if (showUnsavedDialog) {
            ConfirmationDialog(
                title = stringResource(id = R.string.activity_not_saved_title),
                body = stringResource(id = R.string.activity_not_saved_description),
                onDismiss = { showUnsavedDialog = false },
                navigator::navigateUp
            )
        }
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val s = state) {
                is AddParticipantsToActivityState.Loaded -> ParticipantSelectionList(
                    state = s,
                    onSelectParticipant = viewModel::selectParticipant,
                    onUnselectParticipant = viewModel::unselectParticipant,
                    onAddNewParticipant = { navigator.navigate(AddEditParticipantScreenDestination()) },
                    onSelectParticipants = onSelectParticipants,
                    onFilteringByClass = viewModel::setFilteringByClass
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
                Header(header = R.string.participant_selection, modifier = Modifier.padding(all = 16.dp))
            }
            items(state.selection, key = { it.id }) { p ->
                Participant(
                    participant = p,
                    selected = true,
                    onClick = { onUnselectParticipant(p) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        item {
            Column(modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)) {
                Header(header = R.string.participant_list) {
                    OutlinedButton(onClick = onAddNewParticipant) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = stringResource(id = R.string.add)
                            )
                            Text(text = stringResource(id = R.string.add))
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
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onSelectParticipants, modifier = Modifier.align(Alignment.Center)) {
                    Text(text = stringResource(id = R.string.select_number_of_participants, state.selection.size))
                }
            }
        }
    }
}

@Composable
private fun FilterByClassSwitch(
    classes: List<ScoutClass>,
    filteringByClass: Boolean,
    onFilteringByClass: (Boolean) -> Unit
) {
    if (classes.isNotEmpty()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(
                    id = R.string.filtering_by_class,
                    classes.map { it.title }.joinToString()
                ),
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.weight(1f)
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
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = participant.name,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = selected, onCheckedChange = { onClick() },
            colors = CheckboxDefaults.colors(checkedColor = participant.scoutClass.color)
        )
    }
}

@Composable
private fun NoMoreParticipants(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.no_more_participants),
        modifier = modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun Header(
    @StringRes header: Int,
    modifier: Modifier = Modifier,
    endContent: @Composable () -> Unit = {}
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = header),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.weight(1f)
        )
        endContent()
    }
}