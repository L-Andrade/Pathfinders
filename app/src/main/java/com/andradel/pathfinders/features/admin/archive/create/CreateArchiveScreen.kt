package com.andradel.pathfinders.features.admin.archive.create

import androidx.annotation.PluralsRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andradel.pathfinders.R
import com.andradel.pathfinders.extensions.collectChannelFlow
import com.andradel.pathfinders.features.destinations.ArchiveSelectActivitiesManuallyScreenDestination
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.activity.ActivitySelectionArg
import com.andradel.pathfinders.model.title
import com.andradel.pathfinders.ui.ConfirmationDialog
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.validation.ValidationResult
import com.andradel.pathfinders.validation.errorMessage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.delay

@Composable
@Destination
fun CreateArchiveScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<ArchiveSelectActivitiesManuallyScreenDestination, ActivitySelectionArg>,
    viewModel: CreateArchiveViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    resultRecipient.onNavResult { if (it is NavResult.Value) viewModel.select(it.value.selection.toList()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.collectChannelFlow(viewModel.result) { result ->
            result.onFailure { snackbarHostState.showSnackbar(context.getString(R.string.generic_error)) }
        }
    }
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                title = stringResource(id = R.string.create_archive),
                onIconClick = navigator::navigateUp
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val state by viewModel.state.collectAsStateWithLifecycle()
        val progressState by viewModel.progressState.collectAsStateWithLifecycle()
        progressState?.let { progress ->
            if (progress.finished) {
                LaunchedEffect(Unit) {
                    delay(500)
                    navigator.navigateUp()
                }
            }
            ProgressStateDialog(progress)
        }
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 16.dp)) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    NameField(
                        name = state.name,
                        validation = state.nameValidation,
                        onUpdateName = viewModel::onUpdateName,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    AnimatedVisibility(state.participants != null) {
                        if (state.participants != null) {
                            AffectedCheckbox(
                                checked = state.deleteParticipants,
                                affected = requireNotNull(state.participants),
                                checkboxRes = R.plurals.archive_delete_participants,
                                warningRes = R.plurals.archive_participants_in_other_activities,
                                onCheckedChange = viewModel::onCheckDeleteParticipants,
                            )
                        }
                    }
                }
                item {
                    AnimatedVisibility(state.criteria != null) {
                        if (state.criteria != null) {
                            AffectedCheckbox(
                                checked = state.deleteCriteria,
                                affected = requireNotNull(state.criteria),
                                checkboxRes = R.plurals.archive_delete_criteria,
                                warningRes = R.plurals.archive_criteria_in_other_activities,
                                onCheckedChange = viewModel::onCheckDeleteCriteria,
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SelectActivitiesButtons(
                        onSelectByDateRange = viewModel::selectByDateRange,
                        onSelectAll = viewModel::selectAll,
                        onSelectManually = {
                            navigator.navigate(
                                ArchiveSelectActivitiesManuallyScreenDestination(
                                    ActivitySelectionArg(ArrayList(viewModel.activities()))
                                )
                            )
                        },
                    )
                }
                items(state.activities, key = { it.id }) { activity ->
                    ActivityItem(activity = activity, onUnselect = { viewModel.unselectActivity(activity) })
                }
            }
            HorizontalDivider()
            CreateArchiveButton(
                onAdd = viewModel::addArchive,
                canSave = state.canSave,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun CreateArchiveButton(
    onAdd: () -> Unit,
    canSave: Boolean,
    modifier: Modifier = Modifier,
) {
    var showConfirmation by remember { mutableStateOf(false) }
    if (showConfirmation) {
        ConfirmationDialog(
            title = stringResource(id = R.string.archive_confirmation_title),
            body = stringResource(id = R.string.archive_confirmation_description),
            onDismiss = { showConfirmation = false },
            onConfirm = {
                onAdd()
                showConfirmation = false
            }
        )
    }
    Button(
        onClick = { showConfirmation = true },
        enabled = canSave,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_save), contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = R.string.add))
    }
}

@Composable
private fun ProgressStateDialog(progress: CreateArchiveProgressState) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
    ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(modifier = Modifier.padding(all = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(id = R.string.archiving),
                    style = MaterialTheme.typography.titleMedium
                )
                ProgressIndicator(stringResource(id = R.string.creating_archive), progress.deletedActivities)
                ProgressIndicator(stringResource(id = R.string.deleting_activities), progress.deletedActivities)
                ProgressIndicator(stringResource(id = R.string.deleting_participants), progress.deletedParticipants)
                ProgressIndicator(stringResource(id = R.string.deleting_criteria), progress.deletedCriteria)
            }
        }
    }
}

@Composable
private fun ProgressIndicator(text: String, state: ArchiveState?, modifier: Modifier = Modifier) {
    if (state != null) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(8.dp))
            when (state) {
                ArchiveState.InProgress -> CircularProgressIndicator(modifier = Modifier.size(16.dp))
                ArchiveState.Success -> Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                ArchiveState.Fail -> Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun NameField(
    name: String,
    validation: ValidationResult?,
    onUpdateName: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = name,
        onValueChange = onUpdateName,
        label = {
            Text(validation?.errorMessage ?: stringResource(id = R.string.name_hint))
        },
        isError = validation?.errorMessage != null,
        modifier = modifier,
    )
}

@Composable
private fun AffectedCheckbox(
    checked: Boolean,
    affected: Affected,
    @PluralsRes checkboxRes: Int,
    @PluralsRes warningRes: Int,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LabeledCheckbox(
            checked = checked,
            text = pluralStringResource(id = checkboxRes, count = affected.size, affected.size),
            onCheckedChange = onCheckedChange,
        )
        AnimatedVisibility(affected.usedInOtherActivities > 0 && checked) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.large)
                    .padding(all = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = pluralStringResource(
                        id = warningRes,
                        count = affected.usedInOtherActivities,
                        affected.usedInOtherActivities,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActivityItem(
    activity: Activity,
    onUnselect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onUnselect) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = stringResource(id = R.string.delete)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = activity.name, style = MaterialTheme.typography.titleSmall)
            Text(
                text = activity.classes.map { it.title }.joinToString(", "),
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (activity.date != null) {
            Text(text = activity.date.toString(), style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun SelectActivitiesButtons(
    onSelectByDateRange: (Long?, Long?) -> Unit,
    onSelectAll: () -> Unit,
    onSelectManually: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(id = R.string.activity_list),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AddByDateRangeButton(onSelect = onSelectByDateRange, Modifier.weight(1f))
            Button(onClick = onSelectManually, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.add_manually))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onSelectAll, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.add_all))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddByDateRangeButton(
    onSelect: (Long?, Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateRangePicker by remember { mutableStateOf(false) }
    val state = rememberDateRangePickerState()
    Button(onClick = { showDateRangePicker = true }, modifier = modifier) {
        Text(text = stringResource(id = R.string.add_by_date_range))
    }
    if (showDateRangePicker) {
        Dialog(
            onDismissRequest = { showDateRangePicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .padding(horizontal = 8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column {
                    DateRangePicker(
                        state = state,
                        title = {
                            DateRangePickerDefaults.DateRangePickerTitle(
                                displayMode = state.displayMode,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = { showDateRangePicker = false }, modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onSelect(state.selectedStartDateMillis, state.selectedEndDateMillis)
                                state.setSelection(null, null)
                                showDateRangePicker = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.select))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledCheckbox(
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}