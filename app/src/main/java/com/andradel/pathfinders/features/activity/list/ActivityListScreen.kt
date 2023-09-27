package com.andradel.pathfinders.features.activity.list

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
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.andradel.pathfinders.features.destinations.AddEditActivityScreenDestination
import com.andradel.pathfinders.features.destinations.EvaluateActivityScreenDestination
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.title
import com.andradel.pathfinders.ui.ConfirmationDialog
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun ActivityListScreen(
    navigator: DestinationsNavigator,
    viewModel: ActivityListViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = R.string.activity_list,
                onIconClick = { navigator.navigateUp() },
                endContent = {
                    TextButton(onClick = { navigator.navigate(AddEditActivityScreenDestination()) }) {
                        Text(text = stringResource(id = R.string.add_activity))
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val state by viewModel.state.collectAsState()
            when (val s = state) {
                is ActivityListState.Loaded -> LazyColumn {
                    items(s.activities, { it.id }) { activity ->
                        ActivityCard(
                            activity = activity,
                            onEditClick = { navigator.navigate(AddEditActivityScreenDestination(activity)) },
                            onDeleteClick = { viewModel.deleteActivity(activity) },
                            onEvaluateClick = { navigator.navigate(EvaluateActivityScreenDestination(activity)) },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                ActivityListState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ActivityCard(
    activity: Activity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEvaluateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = RoundedCornerShape(8.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var removeDialog by remember { mutableStateOf(false) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f)
                )
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    if (activity.criteria.isNotEmpty() && activity.participants.isNotEmpty()) {
                        IconButton(onClick = onEvaluateClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_evaluate),
                                contentDescription = stringResource(id = R.string.evaluate_activity)
                            )
                        }
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = stringResource(id = R.string.edit_activity)
                        )
                    }
                    IconButton(onClick = { removeDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                }
            }
            if (activity.classes.isNotEmpty()) {
                Text(
                    text = activity.classes.map { it.title }.joinToString(),
                    style = MaterialTheme.typography.body2,
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(id = R.string.participant_number, activity.participants.size),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1f)
                )
                if (activity.date.isNotBlank()) {
                    Text(
                        text = activity.date,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.align(Alignment.Bottom)
                    )
                }
            }
            if (removeDialog) {
                ConfirmationDialog(
                    onDismiss = { removeDialog = false },
                    onConfirm = onDeleteClick,
                    title = stringResource(id = R.string.delete),
                    body = stringResource(id = R.string.delete_confirmation, activity.name)
                )
            }
        }
    }
}