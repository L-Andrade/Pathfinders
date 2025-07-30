package com.andradel.pathfinders.shared.features.activity.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.model.title
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.collectNavResultAsState
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.shared.ui.fields.DatePickerField
import com.andradel.pathfinders.shared.validation.ValidationResult
import com.andradel.pathfinders.shared.validation.errorMessage
import com.andradel.pathfinders.shared.validation.isError
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.activity_not_saved_description
import pathfinders.shared.generated.resources.activity_not_saved_title
import pathfinders.shared.generated.resources.add_activity
import pathfinders.shared.generated.resources.archived_activity
import pathfinders.shared.generated.resources.classes
import pathfinders.shared.generated.resources.create_activity_for_each_class
import pathfinders.shared.generated.resources.criteria
import pathfinders.shared.generated.resources.delete
import pathfinders.shared.generated.resources.delete_confirmation
import pathfinders.shared.generated.resources.edit_activity
import pathfinders.shared.generated.resources.generic_error
import pathfinders.shared.generated.resources.ic_add
import pathfinders.shared.generated.resources.ic_delete
import pathfinders.shared.generated.resources.ic_lock
import pathfinders.shared.generated.resources.ic_save
import pathfinders.shared.generated.resources.name_hint
import pathfinders.shared.generated.resources.no_criteria_selected
import pathfinders.shared.generated.resources.no_participants_selected
import pathfinders.shared.generated.resources.participant_list
import pathfinders.shared.generated.resources.save
import pathfinders.shared.generated.resources.select
import pathfinders.shared.generated.resources.select_all
import pathfinders.shared.generated.resources.unselect_all

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddEditActivityScreen(
    navigator: NavController,
    viewModel: AddEditActivityViewModel = koinViewModel(),
) {
    val selectionResult by navigator.collectNavResultAsState<List<Participant>>(
        NavigationRoute.AddParticipantsToActivity.Result,
    )
    LaunchedEffect(selectionResult) { selectionResult?.let { viewModel.setSelection(it) } }
    val criteriaResult by navigator.collectNavResultAsState<List<ActivityCriteria>>(
        NavigationRoute.AddCriteriaToActivity.Result,
    )
    LaunchedEffect(criteriaResult) { criteriaResult?.let { viewModel.setCriteriaSelection(it) } }
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsState()
    var showUnsavedDialog by remember { mutableStateOf(false) }
    BackHandler {
        if (viewModel.isUnsaved) {
            showUnsavedDialog = true
        } else {
            navigator.navigateUp()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBarTitleWithIcon(
                title = if (viewModel.isEditing) state.name else stringResource(Res.string.add_activity),
                onIconClick = {
                    if (viewModel.isUnsaved) {
                        showUnsavedDialog = true
                    } else {
                        navigator.navigateUp()
                    }
                },
                endContent = {
                    IconButton(enabled = state.isValid, onClick = viewModel::addActivity) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_save),
                            contentDescription = stringResource(Res.string.save),
                        )
                    }
                    DeleteActivityIcon(canDelete = viewModel.isEditing && state.isAdmin, name = state.name) {
                        viewModel.deleteActivity()
                        navigator.navigateUp()
                    }
                },
            )
        },
    ) { padding ->
        var loading by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = state.activityResult) {
            loading = state.activityResult is ActivityResult.Loading
            when (state.activityResult) {
                ActivityResult.Failure -> snackbarHostState.showSnackbar(getString(Res.string.generic_error))
                ActivityResult.Success -> navigator.navigateUp()
                ActivityResult.Loading, null -> Unit
            }
        }
        if (showUnsavedDialog) {
            ConfirmationDialog(
                title = stringResource(Res.string.activity_not_saved_title),
                body = stringResource(Res.string.activity_not_saved_description),
                onDismiss = { showUnsavedDialog = false },
                navigator::navigateUp,
            )
        }
        Column(modifier = Modifier.padding(padding)) {
            AddEditColumn(
                state = state,
                isEditing = viewModel.isEditing,
                onUpdateName = viewModel::updateName,
                onUpdateDate = viewModel::updateDate,
                onCreateForEach = viewModel::setCreateForEach,
                onSetAllSelected = viewModel::setAllSelected,
                onSetClassSelected = viewModel::setClassSelected,
                onSelectCriteria = {
                    navigator.navigate(NavigationRoute.AddCriteriaToActivity(state.criteria))
                },
                onSelectParticipants = {
                    navigator.navigate(
                        NavigationRoute.AddParticipantsToActivity(state.participants, state.classes),
                    )
                },
                modifier = Modifier.weight(1f),
            )
            HorizontalDivider()
            AddEditButton(
                isEditing = viewModel.isEditing,
                onAddActivity = viewModel::addActivity,
                state = state,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
            )
        }
        if (loading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun AddEditColumn(
    state: AddEditActivityState,
    isEditing: Boolean,
    onUpdateName: (String) -> Unit,
    onUpdateDate: (Long) -> Unit,
    onCreateForEach: (Boolean) -> Unit,
    onSetAllSelected: (Boolean) -> Unit,
    onSetClassSelected: (ParticipantClass, Boolean) -> Unit,
    onSelectCriteria: () -> Unit,
    onSelectParticipants: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        if (state.isArchived) {
            item {
                Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(all = 16.dp)) {
                        Icon(painter = painterResource(Res.drawable.ic_lock), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.archived_activity),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.size(16.dp))
            NameField(state.name, state.nameValidation, state.isArchived, onUpdateName)
        }
        item {
            Spacer(modifier = Modifier.size(16.dp))
            DatePickerField(
                dateRepresentation = state.dateRepresentation,
                dateMillis = state.date,
                enabled = !state.isArchived,
                updateDate = onUpdateDate,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        item {
            Spacer(modifier = Modifier.size(16.dp))
            Header(header = Res.string.classes, modifier = Modifier.padding(horizontal = 16.dp)) {
                val isSelected by remember(state) {
                    derivedStateOf { state.classes.size == ParticipantClass.options.size }
                }
                if (!state.isArchived) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(if (isSelected) Res.string.unselect_all else Res.string.select_all),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(checked = isSelected, onCheckedChange = onSetAllSelected, enabled = state.isAdmin)
                    }
                }
            }
            if (!isEditing && state.isAdmin) {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = state.createForEach, onCheckedChange = onCreateForEach)
                    Text(text = stringResource(Res.string.create_activity_for_each_class))
                }
            }
        }
        items(ParticipantClass.options, key = { it.name }) { scoutClass ->
            val isSelected by remember(state) { derivedStateOf { scoutClass in state.classes } }
            ScoutClassCheckbox(
                selected = isSelected,
                participantClass = scoutClass,
                onCheckedChange = { onSetClassSelected(scoutClass, it) },
                enabled = state.isAdmin,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        item {
            Spacer(modifier = Modifier.size(16.dp))
            HeaderWithAddButton(
                header = Res.string.criteria,
                onClick = onSelectCriteria,
                showButton = !state.isArchived,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        if (state.criteria.isNotEmpty()) {
            items(state.criteria, key = { it.id }) { criteria ->
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = criteria.name, modifier = Modifier.padding(horizontal = 16.dp))
            }
        } else {
            item {
                Text(
                    text = stringResource(Res.string.no_criteria_selected),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
        item {
            Spacer(modifier = Modifier.size(8.dp))
            HeaderWithAddButton(
                header = Res.string.participant_list,
                onClick = onSelectParticipants,
                showButton = !state.isArchived,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        if (state.participants.isNotEmpty()) {
            items(state.participants, key = { it.id }) { participant ->
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = participant.name, modifier = Modifier.padding(horizontal = 16.dp))
            }
        } else {
            item {
                Text(
                    text = stringResource(Res.string.no_participants_selected),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun AddEditButton(
    isEditing: Boolean,
    onAddActivity: () -> Unit,
    state: AddEditActivityState,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onAddActivity,
        enabled = state.isValid,
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(stringResource(if (isEditing) Res.string.edit_activity else Res.string.add_activity))
    }
}

@Composable
private fun DeleteActivityIcon(canDelete: Boolean, name: String, onDelete: () -> Unit) {
    if (canDelete) {
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

@Composable
private fun ScoutClassCheckbox(
    selected: Boolean,
    enabled: Boolean,
    participantClass: ParticipantClass,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = selected,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = CheckboxDefaults.colors(checkedColor = participantClass.color),
        )
        Text(text = participantClass.title)
    }
}

@Composable
private fun NameField(
    name: String,
    nameValidation: ValidationResult?,
    readOnly: Boolean,
    onUpdateName: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = name,
        readOnly = readOnly,
        onValueChange = onUpdateName,
        label = {
            Text(nameValidation?.errorMessage ?: stringResource(Res.string.name_hint))
        },
        isError = nameValidation?.isError ?: false,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}

@Composable
private fun HeaderWithAddButton(
    header: StringResource,
    onClick: () -> Unit,
    showButton: Boolean,
    modifier: Modifier = Modifier,
) {
    Header(header = header, modifier = modifier) {
        if (showButton) {
            OutlinedButton(onClick = onClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = stringResource(Res.string.select),
                    )
                    Text(text = stringResource(Res.string.select))
                }
            }
        }
    }
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