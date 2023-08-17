package com.andradel.pathfinders.features.participant.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andradel.pathfinders.R
import com.andradel.pathfinders.features.destinations.AddEditParticipantScreenDestination
import com.andradel.pathfinders.features.destinations.ParticipantProfileScreenDestination
import com.andradel.pathfinders.model.ScoutClass
import com.andradel.pathfinders.model.color
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.model.title
import com.andradel.pathfinders.ui.ConfirmationDialog
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.ui.onColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@Composable
@Destination
fun ParticipantListScreen(
    navigator: DestinationsNavigator,
    viewModel: ParticipantListViewModel = hiltViewModel()
) {
    val isAdmin by viewModel.isAdmin.collectAsState()
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = R.string.participant_list,
                onIconClick = { navigator.navigateUp() },
                endContent = {
                    if (isAdmin) {
                        TextButton(onClick = { navigator.navigate(AddEditParticipantScreenDestination()) }) {
                            Text(text = stringResource(id = R.string.add_participant))
                        }
                    }
                }
            )
        },
        scaffoldState = rememberScaffoldState(),
        content = { padding ->
            val state by viewModel.state.collectAsState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (val s = state) {
                    is ParticipantListState.Loaded -> ParticipantList(
                        section = s.participants,
                        selectedSorting = s.sort,
                        showButtons = isAdmin,
                        onParticipantClick = {
                            navigator.navigate(ParticipantProfileScreenDestination(it))
                        },
                        deleteParticipant = viewModel::deleteParticipant,
                        onEditParticipant = {
                            navigator.navigate(AddEditParticipantScreenDestination(it))
                        },
                        onCollapseSection = viewModel::collapseSection,
                        onSortClick = viewModel::sortBy,
                    )

                    ParticipantListState.Loading ->
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ParticipantList(
    section: List<ParticipantSection>,
    selectedSorting: ParticipantSort,
    showButtons: Boolean,
    onParticipantClick: (Participant) -> Unit,
    deleteParticipant: (Participant) -> Unit,
    onEditParticipant: (Participant) -> Unit,
    onCollapseSection: (ScoutClass) -> Unit,
    onSortClick: (ParticipantSort) -> Unit,
) {
    Box {
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
                item(key = section.scoutClass.name) {
                    Row(
                        verticalAlignment = CenterVertically, modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 12.dp, bottom = 4.dp)
                    ) {
                        Text(
                            text = section.scoutClass.title,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onCollapseSection(section.scoutClass) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_down),
                                contentDescription = null,
                                modifier = Modifier.rotate(if (section.collapsed) 180f else 0f)
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
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }
        SortingFab(isExpanded, selectedSorting,{ isExpanded = !isExpanded }, onSortClick, Modifier.align(BottomEnd))
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun SortingFab(
    isExpanded: Boolean,
    selected: ParticipantSort,
    onExpand: () -> Unit,
    onSortClick: (ParticipantSort) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        ParticipantSort.values().forEach { sort ->
            var delayedExpanded by remember { mutableStateOf(isExpanded) }
            LaunchedEffect(key1 = isExpanded) {
                val first = if (isExpanded) (ParticipantSort.values().size - sort.ordinal) else sort.ordinal
                delay(50L * first)
                delayedExpanded = isExpanded
            }
            AnimatedVisibility(visible = delayedExpanded) {
                Surface(
                    shape = RoundedCornerShape(50),
                    onClick = { onSortClick(sort); onExpand() },
                    color = MaterialTheme.colors.secondary,
                    border = if (sort == selected) BorderStroke(2.dp, MaterialTheme.colors.background) else null,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = CenterVertically) {
                        val (label, icon) = when (sort) {
                            ParticipantSort.PointsAsc -> R.string.participant_sort_points to R.drawable.ic_arrow_up
                            ParticipantSort.PointsDesc -> R.string.participant_sort_points to R.drawable.ic_arrow_down
                            ParticipantSort.NameAsc -> R.string.participant_sort_name to R.drawable.ic_arrow_up
                            ParticipantSort.NameDesc -> R.string.participant_sort_name to R.drawable.ic_arrow_down
                        }
                        Text(
                            text = stringResource(id = label),
                            style = MaterialTheme.typography.button,
                        )
                        Icon(painter = painterResource(id = icon), contentDescription = null)
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            text = {
                Row(verticalAlignment = CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ic_sort), contentDescription = null)
                    AnimatedVisibility(isExpanded) {
                        Text(text = stringResource(id = R.string.participant_sort))
                    }
                }
            },
            onClick = onExpand
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
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
        backgroundColor = participant.participant.scoutClass.color,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        onClick = { onParticipantClick(participant.participant) }
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.padding(all = 16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = participant.participant.name,
                    style = MaterialTheme.typography.h6,
                    color = participant.participant.scoutClass.color.onColor
                )
                Text(
                    text = stringResource(id = R.string.participant_score, participant.score),
                    style = MaterialTheme.typography.subtitle2,
                    color = participant.participant.scoutClass.color.onColor
                )
            }
            if (showButtons) {
                var deleteDialog by remember { mutableStateOf(false) }
                IconButton(onClick = { editParticipant(participant.participant) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = stringResource(id = R.string.edit),
                        tint = participant.participant.scoutClass.color.onColor
                    )
                }
                IconButton(onClick = { deleteDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(id = R.string.delete),
                        tint = participant.participant.scoutClass.color.onColor
                    )
                }
                if (deleteDialog) {
                    ConfirmationDialog(
                        onDismiss = { deleteDialog = false },
                        onConfirm = { deleteParticipant(participant.participant) },
                        title = stringResource(id = R.string.delete),
                        body = stringResource(R.string.delete_confirmation, participant.participant.name)
                    )
                }
            }
        }
    }
}