package com.andradel.pathfinders.shared.features.participant.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.andradel.pathfinders.shared.model.ParticipantClass
import com.andradel.pathfinders.shared.model.color
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.model.title
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.shared.ui.onColor
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.add_participant
import pathfinders.shared.generated.resources.delete
import pathfinders.shared.generated.resources.delete_confirmation
import pathfinders.shared.generated.resources.edit
import pathfinders.shared.generated.resources.ic_arrow_down
import pathfinders.shared.generated.resources.ic_arrow_up
import pathfinders.shared.generated.resources.ic_chevron_down
import pathfinders.shared.generated.resources.ic_delete
import pathfinders.shared.generated.resources.ic_edit
import pathfinders.shared.generated.resources.ic_sort
import pathfinders.shared.generated.resources.participant_list
import pathfinders.shared.generated.resources.participant_score
import pathfinders.shared.generated.resources.participant_sort
import pathfinders.shared.generated.resources.participant_sort_name
import pathfinders.shared.generated.resources.participant_sort_points

@Composable
fun ParticipantListScreen(navigator: NavController, viewModel: ParticipantListViewModel = koinViewModel()) {
    val canModify by viewModel.canModify.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = Res.string.participant_list,
                onIconClick = { navigator.navigateUp() },
                endContent = {
                    if (canModify) {
                        TextButton(onClick = { navigator.navigate(NavigationRoute.AddEditParticipant()) }) {
                            Text(text = stringResource(Res.string.add_participant))
                        }
                    }
                },
            )
        },
        content = { padding ->
            val state by viewModel.state.collectAsState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                when (val s = state) {
                    is ParticipantListState.Loaded -> ParticipantList(
                        section = s.participants,
                        selectedSorting = s.sort,
                        showButtons = canModify,
                        onParticipantClick = {
                            navigator.navigate(NavigationRoute.ParticipantProfile(it, viewModel.archiveName))
                        },
                        deleteParticipant = viewModel::deleteParticipant,
                        onEditParticipant = {
                            navigator.navigate(NavigationRoute.AddEditParticipant(it))
                        },
                        onCollapseSection = viewModel::collapseSection,
                        onSortClick = viewModel::sortBy,
                    )

                    ParticipantListState.Loading ->
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        },
    )
}

@Composable
private fun ParticipantList(
    section: List<ParticipantSection>,
    selectedSorting: ParticipantSort,
    showButtons: Boolean,
    onParticipantClick: (Participant) -> Unit,
    deleteParticipant: (Participant) -> Unit,
    onEditParticipant: (Participant) -> Unit,
    onCollapseSection: (ParticipantClass) -> Unit,
    onSortClick: (ParticipantSort) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        var isExpanded by remember { mutableStateOf(false) }
        val scrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    isExpanded = false
                    return Offset.Zero
                }
            }
        }
        LazyColumn(modifier = Modifier.nestedScroll(scrollConnection), contentPadding = PaddingValues(bottom = 64.dp)) {
            section.forEach { section ->
                item(key = section.participantClass.name) {
                    Row(
                        verticalAlignment = CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 12.dp, bottom = 4.dp),
                    ) {
                        Text(
                            text = section.participantClass.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { onCollapseSection(section.participantClass) }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_chevron_down),
                                contentDescription = null,
                                modifier = Modifier.rotate(if (section.collapsed) 180f else 0f),
                            )
                        }
                    }
                }
                if (!section.collapsed) {
                    items(items = section.participants, key = { it.participant.id }) { participant ->
                        ParticipantCard(
                            participant = participant,
                            showButtons = showButtons,
                            onParticipantClick = onParticipantClick,
                            deleteParticipant = deleteParticipant,
                            editParticipant = onEditParticipant,
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
        SortingFab(isExpanded, selectedSorting, { isExpanded = !isExpanded }, onSortClick, Modifier.align(BottomEnd))
    }
}

@Composable
private fun SortingFab(
    isExpanded: Boolean,
    selected: ParticipantSort,
    onExpand: () -> Unit,
    onSortClick: (ParticipantSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        ParticipantSort.entries.forEach { sort ->
            key(sort.ordinal) {
                var delayedExpanded by remember { mutableStateOf(isExpanded) }
                LaunchedEffect(key1 = isExpanded) {
                    val first = if (isExpanded) (ParticipantSort.entries.size - sort.ordinal) else sort.ordinal
                    delay(50L * first)
                    delayedExpanded = isExpanded
                }
                AnimatedVisibility(visible = delayedExpanded) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        onClick = {
                            onSortClick(sort)
                            onExpand()
                        },
                        color = MaterialTheme.colorScheme.secondary,
                        border = if (sort == selected) {
                            BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.background,
                            )
                        } else {
                            null
                        },
                        modifier = Modifier.padding(4.dp),
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = CenterVertically) {
                            val (label, icon) = when (sort) {
                                ParticipantSort.PointsAsc -> Res.string.participant_sort_points to Res.drawable.ic_arrow_up
                                ParticipantSort.PointsDesc ->
                                    Res.string.participant_sort_points to Res.drawable.ic_arrow_down
                                ParticipantSort.NameAsc -> Res.string.participant_sort_name to Res.drawable.ic_arrow_up
                                ParticipantSort.NameDesc -> Res.string.participant_sort_name to Res.drawable.ic_arrow_down
                            }
                            Text(
                                text = stringResource(label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary,
                            )
                        }
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            content = {
                Row(verticalAlignment = CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_sort),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                    )
                    AnimatedVisibility(isExpanded) {
                        Text(
                            text = stringResource(Res.string.participant_sort),
                            color = MaterialTheme.colorScheme.onSecondary,
                        )
                    }
                }
            },
            onClick = onExpand,
        )
    }
}

@Composable
private fun ParticipantCard(
    participant: ParticipantWithTotalScore,
    showButtons: Boolean,
    onParticipantClick: (Participant) -> Unit,
    deleteParticipant: (Participant) -> Unit,
    editParticipant: (Participant) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = participant.participant.participantClass.color),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        onClick = { onParticipantClick(participant.participant) },
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.padding(all = 16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = participant.participant.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = participant.participant.participantClass.color.onColor,
                )
                Text(
                    text = stringResource(Res.string.participant_score, participant.score),
                    style = MaterialTheme.typography.titleSmall,
                    color = participant.participant.participantClass.color.onColor,
                )
            }
            if (showButtons) {
                var deleteDialog by remember { mutableStateOf(false) }
                IconButton(onClick = { editParticipant(participant.participant) }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = stringResource(Res.string.edit),
                        tint = participant.participant.participantClass.color.onColor,
                    )
                }
                IconButton(onClick = { deleteDialog = true }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delete),
                        contentDescription = stringResource(Res.string.delete),
                        tint = participant.participant.participantClass.color.onColor,
                    )
                }
                if (deleteDialog) {
                    ConfirmationDialog(
                        onDismiss = { deleteDialog = false },
                        onConfirm = { deleteParticipant(participant.participant) },
                        title = stringResource(Res.string.delete),
                        body = stringResource(Res.string.delete_confirmation, participant.participant.name),
                    )
                }
            }
        }
    }
}