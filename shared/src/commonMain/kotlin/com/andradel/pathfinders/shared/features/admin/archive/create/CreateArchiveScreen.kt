package com.andradel.pathfinders.shared.features.admin.archive.create

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andradel.pathfinders.flavors.model.title
import com.andradel.pathfinders.shared.extensions.collectChannelFlow
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.Navigator
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.shared.validation.ValidationResult
import com.andradel.pathfinders.shared.validation.errorMessage
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.activity_list
import pathfinders.shared.generated.resources.add
import pathfinders.shared.generated.resources.add_all
import pathfinders.shared.generated.resources.add_by_date_range
import pathfinders.shared.generated.resources.add_manually
import pathfinders.shared.generated.resources.archive_confirmation_description
import pathfinders.shared.generated.resources.archive_confirmation_title
import pathfinders.shared.generated.resources.archive_criteria_in_other_activities
import pathfinders.shared.generated.resources.archive_delete_criteria
import pathfinders.shared.generated.resources.archive_delete_participants
import pathfinders.shared.generated.resources.archive_participants_in_other_activities
import pathfinders.shared.generated.resources.archiving
import pathfinders.shared.generated.resources.cancel
import pathfinders.shared.generated.resources.create_archive
import pathfinders.shared.generated.resources.creating_archive
import pathfinders.shared.generated.resources.delete
import pathfinders.shared.generated.resources.deleting_activities
import pathfinders.shared.generated.resources.deleting_criteria
import pathfinders.shared.generated.resources.deleting_participants
import pathfinders.shared.generated.resources.generic_error
import pathfinders.shared.generated.resources.ic_delete
import pathfinders.shared.generated.resources.ic_save
import pathfinders.shared.generated.resources.ic_warning
import pathfinders.shared.generated.resources.name_hint
import pathfinders.shared.generated.resources.select

@Composable
fun CreateArchiveScreen(navigator: Navigator, viewModel: CreateArchiveViewModel = koinViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val selectionResult = navigator.resultStore.getResultStateAndRemove<List<Activity>>(
        NavigationRoute.ArchiveSelectActivitiesManually.Result,
    )
    LaunchedEffect(selectionResult) { selectionResult?.let { viewModel.select(it) } }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.collectChannelFlow(viewModel.result) { result ->
            result.onFailure { snackbarHostState.showSnackbar(getString(Res.string.generic_error)) }
        }
    }
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                title = stringResource(Res.string.create_archive),
                onIconClick = navigator::goBack,
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
                    navigator.goBack()
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
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    AnimatedVisibility(state.participants != null) {
                        if (state.participants != null) {
                            AffectedCheckbox(
                                checked = state.deleteParticipants,
                                affected = requireNotNull(state.participants),
                                checkboxRes = Res.plurals.archive_delete_participants,
                                warningRes = Res.plurals.archive_participants_in_other_activities,
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
                                checkboxRes = Res.plurals.archive_delete_criteria,
                                warningRes = Res.plurals.archive_criteria_in_other_activities,
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
                            navigator.navigate(NavigationRoute.ArchiveSelectActivitiesManually(viewModel.activities()))
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
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun CreateArchiveButton(onAdd: () -> Unit, canSave: Boolean, modifier: Modifier = Modifier) {
    var showConfirmation by remember { mutableStateOf(false) }
    if (showConfirmation) {
        ConfirmationDialog(
            title = stringResource(Res.string.archive_confirmation_title),
            body = stringResource(Res.string.archive_confirmation_description),
            onDismiss = { showConfirmation = false },
            onConfirm = {
                onAdd()
                showConfirmation = false
            },
        )
    }
    Button(
        onClick = { showConfirmation = true },
        enabled = canSave,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Icon(painter = painterResource(Res.drawable.ic_save), contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(Res.string.add))
    }
}

@Composable
private fun ProgressStateDialog(progress: CreateArchiveProgressState) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false),
    ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(modifier = Modifier.padding(all = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(Res.string.archiving),
                    style = MaterialTheme.typography.titleMedium,
                )
                ProgressIndicator(stringResource(Res.string.creating_archive), progress.deletedActivities)
                ProgressIndicator(stringResource(Res.string.deleting_activities), progress.deletedActivities)
                ProgressIndicator(stringResource(Res.string.deleting_participants), progress.deletedParticipants)
                ProgressIndicator(stringResource(Res.string.deleting_criteria), progress.deletedCriteria)
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
                    tint = MaterialTheme.colorScheme.primary,
                )

                ArchiveState.Fail -> Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
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
            Text(validation?.errorMessage ?: stringResource(Res.string.name_hint))
        },
        isError = validation?.errorMessage != null,
        modifier = modifier,
    )
}

@Composable
private fun AffectedCheckbox(
    checked: Boolean,
    affected: Affected,
    checkboxRes: PluralStringResource,
    warningRes: PluralStringResource,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        LabeledCheckbox(
            checked = checked,
            text = pluralStringResource(checkboxRes, affected.size, affected.size),
            onCheckedChange = onCheckedChange,
        )
        AnimatedVisibility(affected.usedInOtherActivities > 0 && checked) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.large)
                    .padding(all = 8.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_warning),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = pluralStringResource(
                        warningRes,
                        affected.usedInOtherActivities,
                        affected.usedInOtherActivities,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ActivityItem(activity: Activity, onUnselect: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onUnselect) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = stringResource(Res.string.delete),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = activity.name, style = MaterialTheme.typography.titleSmall)
            Text(
                text = activity.classes.map { it.title }.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
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
            text = stringResource(Res.string.activity_list),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AddByDateRangeButton(onSelect = onSelectByDateRange, Modifier.weight(1f))
            Button(onClick = onSelectManually, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(Res.string.add_manually))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onSelectAll, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(Res.string.add_all))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddByDateRangeButton(onSelect: (Long?, Long?) -> Unit, modifier: Modifier = Modifier) {
    var showDateRangePicker by remember { mutableStateOf(false) }
    val state = rememberDateRangePickerState()
    Button(onClick = { showDateRangePicker = true }, modifier = modifier) {
        Text(text = stringResource(Res.string.add_by_date_range))
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
                shape = MaterialTheme.shapes.medium,
            ) {
                Column {
                    DateRangePicker(
                        state = state,
                        title = {
                            DateRangePickerDefaults.DateRangePickerTitle(
                                displayMode = state.displayMode,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        },
                        modifier = Modifier.weight(1f),
                    )
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(onClick = { showDateRangePicker = false }, modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(Res.string.cancel))
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onSelect(state.selectedStartDateMillis, state.selectedEndDateMillis)
                                state.setSelection(null, null)
                                showDateRangePicker = false
                            },
                        ) {
                            Text(text = stringResource(Res.string.select))
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
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}