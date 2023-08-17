package com.andradel.pathfinders.features.activity.evaluate

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Slider
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andradel.pathfinders.R
import com.andradel.pathfinders.model.activity.ActivityArg
import com.andradel.pathfinders.model.activity.ActivityCriteria
import com.andradel.pathfinders.model.activity.CriteriaScore
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.ui.ConfirmationDialog
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
@Destination(navArgsDelegate = ActivityArg::class)
fun EvaluateActivityScreen(
    navigator: DestinationsNavigator,
    viewModel: EvaluateActivityViewModel = hiltViewModel()
) {
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
                titleRes = R.string.evaluate_activity,
                onIconClick = {
                    if (viewModel.isUnsaved) {
                        showUnsavedDialog = true
                    } else {
                        navigator.navigateUp()
                    }
                },
                elevation = 0.dp,
                endContent = {
                    TextButton(onClick = {
                        viewModel.updateActivityScores()
                        navigator.navigateUp()
                    }) {
                        Text(text = stringResource(id = R.string.done))
                    }
                }
            )
        }
    ) { padding ->
        if (showUnsavedDialog) {
            ConfirmationDialog(
                title = stringResource(id = R.string.activity_not_saved_title),
                body = stringResource(id = R.string.activity_not_saved_description),
                onDismiss = { showUnsavedDialog = false },
                navigator::navigateUp
            )
        }
        val pagerState = rememberPagerState()
        Column(modifier = Modifier.padding(padding)) {
            val activity = remember { viewModel.activity }
            ParticipantTabs(pagerState, activity.participants)
            HorizontalPager(
                count = viewModel.activity.participants.size,
                state = pagerState,
            ) { page ->
                val state by viewModel.state.collectAsState()
                val participant = remember(activity, page) { activity.participants[page] }
                val participantCriteriaScores by remember(state) { derivedStateOf { state[participant.id].orEmpty() } }
                ParticipantTab(
                    criteria = activity.criteria,
                    scores = participantCriteriaScores,
                    onUpdateScore = { criteriaId, score -> viewModel.setScore(participant, criteriaId, score) }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun ParticipantTabs(
    pagerState: PagerState,
    participants: List<Participant>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, tabPositions))
        },
        edgePadding = 8.dp,
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp)
    ) {
        participants.forEachIndexed { index, participant ->
            Tab(
                text = { Text(participant.name) },
                selected = pagerState.currentPage == index,
                onClick = { scope.launch { pagerState.scrollToPage(index) } },
            )
        }
    }
}

@Composable
private fun ParticipantTab(
    criteria: List<ActivityCriteria>,
    scores: CriteriaScore,
    onUpdateScore: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier.fillMaxSize()) {
        item {
            Box(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.total, scores.values.sum()),
                    style = MaterialTheme.typography.h6,
                )
            }
        }
        items(criteria) { c ->
            var value by remember { mutableStateOf(scores[c.id]?.toFloat() ?: 0f) }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = stringResource(id = R.string.criteria_with_value, c.name, scores[c.id] ?: 0),
                    style = MaterialTheme.typography.subtitle2
                )
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    onValueChangeFinished = {
                        value = value.roundToInt().toFloat()
                        onUpdateScore(c.id, value.toInt())
                    },
                    valueRange = 0f..c.maxScore.toFloat(),
                    steps = c.maxScore - 1
                )
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }
    }
}