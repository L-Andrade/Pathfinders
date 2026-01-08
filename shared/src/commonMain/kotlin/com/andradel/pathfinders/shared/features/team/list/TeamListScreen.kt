package com.andradel.pathfinders.shared.features.team.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andradel.pathfinders.shared.features.team.TeamInfo
import com.andradel.pathfinders.shared.model.team.Team
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.NavigationRoute.AddEditTeam
import com.andradel.pathfinders.shared.nav.Navigator
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.add_team
import pathfinders.shared.generated.resources.delete
import pathfinders.shared.generated.resources.delete_confirmation
import pathfinders.shared.generated.resources.edit
import pathfinders.shared.generated.resources.ic_delete
import pathfinders.shared.generated.resources.ic_edit
import pathfinders.shared.generated.resources.team_list
import pathfinders.shared.generated.resources.teams_empty

@Composable
fun TeamListScreen(
    archiveName: String?,
    navigator: Navigator,
    viewModel: TeamListViewModel = koinViewModel { parametersOf(archiveName) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = Res.string.team_list,
                onIconClick = { navigator.goBack() },
                endContent = {
                    if (state.canAdd) {
                        TextButton(onClick = { navigator.navigate(NavigationRoute.AddEditTeam()) }) {
                            Text(text = stringResource(Res.string.add_team))
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
                is TeamListState.Loaded -> LazyColumn {
                    items(s.teams, { it.team.id }) { item ->
                        TeamCard(
                            item = item,
                            canDelete = s.canDelete,
                            onClick = { navigator.navigate(NavigationRoute.TeamProfile(it, archiveName)) },
                            onDeleteClick = { viewModel.delete(it) },
                            onEditClick = { navigator.navigate(AddEditTeam(it, archiveName)) },
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }

                TeamListState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is TeamListState.Empty -> Text(
                    text = stringResource(Res.string.teams_empty),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun TeamCard(
    item: TeamItem,
    canDelete: Boolean,
    onClick: (Team) -> Unit,
    onDeleteClick: (String) -> Unit,
    onEditClick: (Team) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier, shape = RoundedCornerShape(8.dp), onClick = { onClick(item.team) }) {
        Column(modifier = Modifier.padding(16.dp)) {
            var removeDialog by remember { mutableStateOf(false) }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.team.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                if (canDelete) {
                    IconButton(onClick = { removeDialog = true }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = stringResource(Res.string.delete),
                        )
                    }
                    IconButton(onClick = { onEditClick(item.team) }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_edit),
                            contentDescription = stringResource(Res.string.edit),
                        )
                    }
                }
            }
            TeamInfo(participants = item.team.participants.size, points = item.points)
            if (removeDialog) {
                ConfirmationDialog(
                    onDismiss = { removeDialog = false },
                    onConfirm = { onDeleteClick(item.team.id) },
                    title = stringResource(Res.string.delete),
                    body = stringResource(Res.string.delete_confirmation, item.team.name),
                )
            }
        }
    }
}