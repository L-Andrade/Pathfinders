package com.andradel.pathfinders.features.activity.add

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andradel.pathfinders.R
import com.andradel.pathfinders.features.destinations.AddCriteriaToActivityScreenDestination
import com.andradel.pathfinders.features.destinations.AddParticipantsToActivityScreenDestination
import com.andradel.pathfinders.model.ScoutClass
import com.andradel.pathfinders.model.activity.CriteriaSelectionArg
import com.andradel.pathfinders.model.activity.OptionalActivityArg
import com.andradel.pathfinders.model.activity.ParticipantSelectionArg
import com.andradel.pathfinders.model.color
import com.andradel.pathfinders.model.title
import com.andradel.pathfinders.ui.ConfirmationDialog
import com.andradel.pathfinders.ui.DatePicker
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.validation.errorMessage
import com.andradel.pathfinders.validation.isError
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import java.time.LocalDate

@Composable
@Destination(navArgsDelegate = OptionalActivityArg::class)
fun AddEditActivityScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<AddParticipantsToActivityScreenDestination, ParticipantSelectionArg>,
    criteriaRecipient: ResultRecipient<AddCriteriaToActivityScreenDestination, CriteriaSelectionArg>,
    viewModel: AddEditActivityViewModel = hiltViewModel()
) {
    resultRecipient.onNavResult { result ->
        if (result is NavResult.Value) viewModel.setSelection(result.value.selection)
    }
    criteriaRecipient.onNavResult { result ->
        if (result is NavResult.Value) viewModel.setCriteriaSelection(result.value.selection.toList())
    }
    val scaffoldState = rememberScaffoldState()
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
        topBar = {
            TopAppBarTitleWithIcon(
                title = if (viewModel.isEditing) state.name else stringResource(id = R.string.add_activity),
                onIconClick = {
                    if (viewModel.isUnsaved) {
                        showUnsavedDialog = true
                    } else {
                        navigator.navigateUp()
                    }
                },
                endContent = {
                    IconButton(onClick = viewModel::addActivity) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_save),
                            contentDescription = stringResource(id = R.string.save)
                        )
                    }
                    DeleteActivityIcon(viewModel.isEditing, state.name) {
                        viewModel.deleteActivity()
                        navigator.navigateUp()
                    }
                }
            )
        },
        scaffoldState = scaffoldState
    ) { padding ->
        LaunchedEffect(key1 = state.addActivityResult) {
            when (state.addActivityResult) {
                AddActivityResult.Failure -> scaffoldState.snackbarHostState.showSnackbar("Error")
                AddActivityResult.Success -> navigator.navigateUp()
                null -> Unit
            }
        }
        if (showUnsavedDialog) {
            ConfirmationDialog(
                title = stringResource(id = R.string.activity_not_saved_title),
                body = stringResource(id = R.string.activity_not_saved_description),
                onDismiss = { showUnsavedDialog = false },
                navigator::navigateUp
            )
        }
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                Spacer(modifier = Modifier.size(16.dp))
                NameField(state, viewModel)
            }
            item {
                Spacer(modifier = Modifier.size(16.dp))
                DateField(state.date, viewModel::updateDate, modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                Spacer(modifier = Modifier.size(16.dp))
                Header(header = R.string.classes, modifier = Modifier.padding(horizontal = 16.dp)) {
                    val isSelected by remember { derivedStateOf { state.classes.size == ScoutClass.options.size } }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = if (isSelected) R.string.unselect_all else R.string.select_all),
                            style = MaterialTheme.typography.subtitle2
                        )
                        Switch(checked = isSelected, onCheckedChange = viewModel::setAllSelected)
                    }
                }
            }
            items(ScoutClass.options, key = { it.name }) { scoutClass ->
                val isSelected by remember { derivedStateOf { scoutClass in state.classes } }
                ScoutClassCheckbox(
                    selected = isSelected,
                    scoutClass = scoutClass,
                    onCheckedChange = { viewModel.setClassSelected(scoutClass, it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.size(16.dp))
                HeaderWithAddButton(
                    header = R.string.criteria,
                    onClick = { navigator.navigate(AddCriteriaToActivityScreenDestination(ArrayList(state.criteria))) },
                    modifier = Modifier.padding(horizontal = 16.dp)
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
                        text = stringResource(id = R.string.no_criteria_selected),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.size(8.dp))
                HeaderWithAddButton(
                    header = R.string.participant_list,
                    onClick = {
                        navigator.navigate(
                            AddParticipantsToActivityScreenDestination(
                                ParticipantSelectionArg(state.participants, state.classes)
                            )
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
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
                        text = stringResource(id = R.string.no_participants_selected),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.size(16.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = viewModel::addActivity,
                        enabled = state.isValid,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterEnd)
                            .padding(horizontal = 16.dp)
                    ) {
                        val stringId = if (viewModel.isEditing) R.string.edit_activity else R.string.add_activity
                        Text(stringResource(id = stringId))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteActivityIcon(
    isEditing: Boolean,
    name: String,
    onDelete: () -> Unit,
) {
    if (isEditing) {
        var deleteDialog by remember { mutableStateOf(false) }
        IconButton(onClick = { deleteDialog = true }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = stringResource(id = R.string.delete)
            )
        }
        if (deleteDialog) {
            ConfirmationDialog(
                onDismiss = { deleteDialog = false },
                onConfirm = onDelete,
                title = stringResource(id = R.string.delete),
                body = stringResource(id = R.string.delete_confirmation, name)
            )
        }
    }
}

@Composable
private fun ScoutClassCheckbox(
    selected: Boolean,
    scoutClass: ScoutClass,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = selected,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = scoutClass.color)
        )
        Text(text = scoutClass.title)
    }
}

@Composable
private fun DateField(
    date: LocalDate?,
    updateDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showingDatePicker by remember { mutableStateOf(false) }
    val backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity)
    Box(
        modifier = modifier
            .clickable { showingDatePicker = true }
            .clip(RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp))
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(all = 16.dp)
    ) {
        Text(date?.toString() ?: stringResource(id = R.string.date))
    }
    if (showingDatePicker) {
        DatePicker(
            date = date ?: LocalDate.now(),
            onDateSelected = updateDate,
            onDismiss = { showingDatePicker = false }
        )
    }
}

@Composable
private fun NameField(
    state: AddEditActivityState,
    viewModel: AddEditActivityViewModel
) {
    TextField(
        value = state.name,
        onValueChange = viewModel::updateName,
        label = {
            Text(state.nameValidation?.errorMessage ?: stringResource(id = R.string.name_hint))
        },
        isError = state.nameValidation?.isError ?: false,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

@Composable
private fun HeaderWithAddButton(
    @StringRes header: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Header(header = header, modifier = modifier) {
        OutlinedButton(onClick = onClick) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = stringResource(id = R.string.select)
                )
                Text(text = stringResource(id = R.string.select))
            }
        }
    }
}

@Composable
private fun Header(
    @StringRes header: Int,
    modifier: Modifier = Modifier,
    endContent: @Composable () -> Unit = {},
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