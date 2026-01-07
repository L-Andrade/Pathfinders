package com.andradel.pathfinders.shared.features.activity.evaluate.team

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andradel.pathfinders.shared.extensions.collectChannelFlow
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.model.activity.ParticipantScores
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.model.team.Team
import com.andradel.pathfinders.shared.nav.Navigator
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.shared.ui.theme.Typography
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.activity_not_saved_description
import pathfinders.shared.generated.resources.activity_not_saved_title
import pathfinders.shared.generated.resources.criteria_with_value
import pathfinders.shared.generated.resources.done
import pathfinders.shared.generated.resources.evaluate_activity
import pathfinders.shared.generated.resources.evaluate_activity_empty
import pathfinders.shared.generated.resources.generic_error
import pathfinders.shared.generated.resources.total_points
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EvaluateTeamActivityScreen(
    activity: Activity,
    navigator: Navigator,
    viewModel: EvaluateTeamActivityViewModel = koinViewModel { parametersOf(activity) },
) {
    var showUnsavedDialog by remember { mutableStateOf(false) }
    BackHandler {
        if (viewModel.isUnsaved) {
            showUnsavedDialog = true
        } else {
            navigator.goBack()
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.collectChannelFlow(viewModel.result) { result ->
            result.onSuccess {
                navigator.goBack()
            }.onFailure {
                snackbarHostState.showSnackbar(getString(Res.string.generic_error))
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = Res.string.evaluate_activity,
                onIconClick = {
                    if (viewModel.isUnsaved) {
                        showUnsavedDialog = true
                    } else {
                        navigator.goBack()
                    }
                },
                endContent = {
                    when (val s = state) {
                        is EvaluateTeamActivityState.Loaded -> TextButton(
                            onClick = viewModel::onSave,
                            enabled = !s.loading,
                        ) {
                            Text(text = stringResource(Res.string.done))
                        }

                        EvaluateTeamActivityState.Loading, EvaluateTeamActivityState.Empty -> Unit
                    }
                },
            )
        },
    ) { padding ->
        when (val s = state) {
            is EvaluateTeamActivityState.Loaded -> {
                if (showUnsavedDialog) {
                    ConfirmationDialog(
                        title = stringResource(Res.string.activity_not_saved_title),
                        body = stringResource(Res.string.activity_not_saved_description),
                        onDismiss = { showUnsavedDialog = false },
                        navigator::goBack,
                    )
                }
                val pagerState = rememberPagerState { s.teams.size }
                Column(modifier = Modifier.padding(padding)) {
                    TeamTabs(current = pagerState.currentPage) {
                        val scope = rememberCoroutineScope()
                        s.teams.fastForEachIndexed { index, team ->
                            Tab(
                                text = { Text(team.name) },
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            )
                        }
                    }
                    HorizontalPager(state = pagerState) { page ->
                        val team = remember(activity, page) { s.teams[page] }
                        TeamTab(
                            team = team,
                            activity = activity,
                            scores = { s.scores[team.id].orEmpty() },
                            onSetParticipantScore = { participant, criteriaId, score ->
                                viewModel.setParticipantScore(team, participant, criteriaId, score)
                            },
                            onSetTeamScore = { criteriaId, score ->
                                viewModel.setTeamScore(team, criteriaId, score)
                            },
                        )
                    }
                }
            }

            EvaluateTeamActivityState.Loading -> Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            EvaluateTeamActivityState.Empty -> Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                Text(
                    text = stringResource(Res.string.evaluate_activity_empty),
                    style = Typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun TeamTabs(current: Int, modifier: Modifier = Modifier, tabs: @Composable () -> Unit) {
    SecondaryScrollableTabRow(
        selectedTabIndex = current,
        indicator = {
            TabRowDefaults.SecondaryIndicator(modifier = Modifier.tabIndicatorOffset(current))
        },
        edgePadding = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp),
        tabs = tabs,
    )
}

@Composable
private fun TeamTab(
    team: Team,
    activity: Activity,
    scores: () -> ParticipantScores,
    onSetParticipantScore: (Participant, String, Int) -> Unit,
    onSetTeamScore: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isArchived by remember { derivedStateOf { activity.archiveName != null } }
    LazyColumn(modifier.fillMaxSize()) {
        items(activity.criteria) { c ->
            Column(modifier = Modifier.padding(all = 16.dp)) {
                val total = scores().values.sumOf { it[c.id] ?: 0 }
                var value by remember(total) { mutableFloatStateOf(total / team.participants.size.toFloat()) }
                Row {
                    Text(text = c.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.total_points, total),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = value,
                    enabled = !isArchived,
                    onValueChange = { value = it },
                    onValueChangeFinished = {
                        value = value.roundToInt().toFloat()
                        onSetTeamScore(c.id, value.toInt())
                    },
                    valueRange = remember(c) { 0f..c.maxScore.toFloat() },
                    steps = remember(c) { c.maxScore - 1 },
                )
                team.participants.fastForEach { participant ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        var participantValue by remember(value) {
                            mutableFloatStateOf(
                                scores()[participant.id]?.get(c.id)?.toFloat()
                                    ?: activity.scores[participant.id]?.get(c.id)?.toFloat() ?: 0f,
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(
                                Res.string.criteria_with_value, participant.name, participantValue.roundToInt(),
                            ),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Slider(
                            value = participantValue,
                            enabled = !isArchived,
                            onValueChange = { participantValue = it },
                            onValueChangeFinished = {
                                participantValue = participantValue.roundToInt().toFloat()
                                onSetParticipantScore(participant, c.id, participantValue.toInt())
                            },
                            valueRange = remember(c) { 0f..c.maxScore.toFloat() },
                            steps = remember(c) { c.maxScore - 1 },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
            }
        }
    }
}