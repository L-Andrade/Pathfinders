package com.andradel.pathfinders.shared.features.activity.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.andradel.pathfinders.flavors.model.title
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.activity_list
import pathfinders.shared.generated.resources.add_activity
import pathfinders.shared.generated.resources.delete
import pathfinders.shared.generated.resources.delete_confirmation
import pathfinders.shared.generated.resources.evaluate_activity
import pathfinders.shared.generated.resources.ic_delete
import pathfinders.shared.generated.resources.ic_evaluate
import pathfinders.shared.generated.resources.participant_number

@Composable
fun ActivityListScreen(navigator: NavController, viewModel: ActivityListViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = Res.string.activity_list,
                onIconClick = { navigator.navigateUp() },
                endContent = {
                    val canAdd by remember { derivedStateOf { (state as? ActivityListState.Loaded)?.canAdd ?: false } }
                    if (canAdd) {
                        TextButton(onClick = { navigator.navigate(NavigationRoute.AddEditActivity()) }) {
                            Text(text = stringResource(Res.string.add_activity))
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            when (val s = state) {
                is ActivityListState.Loaded -> LazyColumn {
                    items(s.activities, { it.id }) { activity ->
                        ActivityCard(
                            activity = activity,
                            canDelete = s.canDelete,
                            onEditClick = {
                                navigator.navigate(NavigationRoute.AddEditActivity(activity.id, activity.archiveName))
                            },
                            onDeleteClick = { viewModel.deleteActivity(activity) },
                            onEvaluateClick = { navigator.navigate(NavigationRoute.EvaluateActivity(activity)) },
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }

                ActivityListState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun ActivityCard(
    activity: Activity,
    canDelete: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEvaluateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier, shape = RoundedCornerShape(8.dp), onClick = onEditClick) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            var removeDialog by remember { mutableStateOf(false) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    if (activity.criteria.isNotEmpty() && activity.participants.isNotEmpty()) {
                        IconButton(onClick = onEvaluateClick) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_evaluate),
                                contentDescription = stringResource(Res.string.evaluate_activity),
                            )
                        }
                    }
                    if (canDelete) {
                        IconButton(onClick = { removeDialog = true }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_delete),
                                contentDescription = stringResource(Res.string.delete),
                            )
                        }
                    }
                }
            }
            if (activity.classes.isNotEmpty()) {
                Text(
                    text = activity.classes.map { it.title }.joinToString(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(Res.string.participant_number, activity.participants.size),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                )
                if (activity.date != null) {
                    Text(
                        text = activity.date.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.Bottom),
                    )
                }
            }
            if (removeDialog) {
                ConfirmationDialog(
                    onDismiss = { removeDialog = false },
                    onConfirm = onDeleteClick,
                    title = stringResource(Res.string.delete),
                    body = stringResource(Res.string.delete_confirmation, activity.name),
                )
            }
        }
    }
}