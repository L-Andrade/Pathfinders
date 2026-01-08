package com.andradel.pathfinders.shared.features.team.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.model.participant.SelectedParticipants
import com.andradel.pathfinders.shared.model.team.Team
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.Navigator
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.shared.ui.header.HeaderWithAddButton
import com.andradel.pathfinders.shared.validation.errorMessage
import com.andradel.pathfinders.shared.validation.isError
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.add_team
import pathfinders.shared.generated.resources.delete
import pathfinders.shared.generated.resources.delete_confirmation
import pathfinders.shared.generated.resources.edit_team
import pathfinders.shared.generated.resources.generic_error
import pathfinders.shared.generated.resources.ic_delete
import pathfinders.shared.generated.resources.ic_save
import pathfinders.shared.generated.resources.name_hint
import pathfinders.shared.generated.resources.no_participants_selected
import pathfinders.shared.generated.resources.participant_list
import pathfinders.shared.generated.resources.save

@Composable
fun AddEditTeamScreen(
    team: Team?,
    archiveName: String?,
    navigator: Navigator,
    viewModel: AddEditTeamViewModel = koinViewModel { parametersOf(team, archiveName) },
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsState()
    val selectionResult = navigator.resultStore.getResultStateAndRemove<List<Participant>>(
        NavigationRoute.AddParticipantsToActivity.Result,
    )
    LaunchedEffect(selectionResult) { selectionResult?.let { viewModel.setSelection(it) } }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarTitleWithIcon(
                title = if (viewModel.isEditing) state.name else stringResource(Res.string.add_team),
                onIconClick = { navigator.goBack() },
                endContent = {
                    IconButton(enabled = state.isValid, onClick = viewModel::onSave) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_save),
                            contentDescription = stringResource(Res.string.save),
                        )
                    }
                    DeleteTeamIcon(viewModel.isEditing, state.name) {
                        viewModel.delete()
                        navigator.goBack()
                    }
                },
            )
        },
        content = { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (state.teamResult) {
                    TeamResult.Failure -> LaunchedEffect(Unit) {
                        snackbarHostState.showSnackbar(getString(Res.string.generic_error))
                    }

                    TeamResult.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    TeamResult.Success -> LaunchedEffect(Unit) { navigator.goBack() }
                    null -> Unit
                }
                AddEditForm(
                    state = state,
                    isEditing = viewModel.isEditing,
                    isArchived = viewModel.isArchived,
                    onSave = viewModel::onSave,
                    onSetName = viewModel::setName,
                    onSelectParticipants = {
                        navigator.navigate(
                            NavigationRoute.AddParticipantsToActivity(
                                SelectedParticipants(state.participants, emptyList()),
                            ),
                        )
                    },
                )
            }
        },
    )
}

@Composable
private fun AddEditForm(
    state: AddEditTeamState,
    isArchived: Boolean,
    isEditing: Boolean,
    onSave: () -> Unit,
    onSetName: (String) -> Unit,
    onSelectParticipants: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp),
    ) {
        TextField(
            value = state.name,
            onValueChange = onSetName,
            label = { Text(state.nameValidation.errorMessage ?: stringResource(Res.string.name_hint)) },
            isError = state.nameValidation.isError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.size(24.dp))
        HeaderWithAddButton(
            header = stringResource(Res.string.participant_list),
            onClick = onSelectParticipants,
            showButton = !isArchived,
        )
        if (state.participants.isNotEmpty()) {
            state.participants.fastForEach { participant ->
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = participant.name)
            }
        } else {
            Text(
                text = stringResource(Res.string.no_participants_selected),
                modifier = Modifier.padding(vertical = 16.dp),
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        Button(onClick = onSave, enabled = state.isValid, modifier = Modifier.align(Alignment.End)) {
            val stringId = if (isEditing) Res.string.edit_team else Res.string.add_team
            Text(stringResource(stringId))
        }
    }
}

@Composable
private fun DeleteTeamIcon(isEditing: Boolean, name: String, onDelete: () -> Unit) {
    if (isEditing) {
        var deleteDialog by remember { mutableStateOf(false) }
        IconButton(onClick = { deleteDialog = true }) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = stringResource(Res.string.delete),
            )
        }
        if (deleteDialog) {
            ConfirmationDialog(
                onDismiss = { deleteDialog = false },
                onConfirm = onDelete,
                title = stringResource(Res.string.delete),
                body = stringResource(Res.string.delete_confirmation, name),
            )
        }
    }
}