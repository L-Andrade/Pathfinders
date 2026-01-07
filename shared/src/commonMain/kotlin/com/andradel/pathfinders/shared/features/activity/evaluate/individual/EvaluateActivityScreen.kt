package com.andradel.pathfinders.shared.features.activity.evaluate.individual

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.andradel.pathfinders.shared.extensions.collectChannelFlow
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.model.activity.CriteriaScore
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.nav.Navigator
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
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
import pathfinders.shared.generated.resources.generic_error
import pathfinders.shared.generated.resources.total_points
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EvaluateActivityScreen(
    activity: Activity,
    navigator: Navigator,
    viewModel: EvaluateActivityViewModel = koinViewModel { parametersOf(activity) },
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
                    val loading by viewModel.loading.collectAsState()
                    TextButton(onClick = viewModel::onSave, enabled = !loading) {
                        Text(text = stringResource(Res.string.done))
                    }
                },
            )
        },
    ) { padding ->
        if (showUnsavedDialog) {
            ConfirmationDialog(
                title = stringResource(Res.string.activity_not_saved_title),
                body = stringResource(Res.string.activity_not_saved_description),
                onDismiss = { showUnsavedDialog = false },
                navigator::goBack,
            )
        }
        val pagerState = rememberPagerState { activity.participants.size }
        Column(modifier = Modifier.padding(padding)) {
            ParticipantTabs(pagerState, activity.participants)
            HorizontalPager(state = pagerState) { page ->
                val state by viewModel.state.collectAsState()
                val participant = remember(activity, page) { activity.participants[page] }
                val participantCriteriaScores by remember(state) { derivedStateOf { state[participant.id].orEmpty() } }
                ParticipantTab(
                    criteria = activity.criteria,
                    isArchived = activity.archiveName != null,
                    scores = participantCriteriaScores,
                    onUpdateScore = { criteriaId, score -> viewModel.setScore(participant, criteriaId, score) },
                )
            }
        }
    }
}

@Composable
private fun ParticipantTabs(pagerState: PagerState, participants: List<Participant>, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    SecondaryScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            TabRowDefaults.SecondaryIndicator(modifier = Modifier.tabIndicatorOffset(pagerState.currentPage))
        },
        edgePadding = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp),
    ) {
        participants.forEachIndexed { index, participant ->
            Tab(
                text = { Text(participant.name) },
                selected = pagerState.currentPage == index,
                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
            )
        }
    }
}

@Composable
private fun ParticipantTab(
    criteria: List<ActivityCriteria>,
    isArchived: Boolean,
    scores: CriteriaScore,
    onUpdateScore: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier.fillMaxSize()) {
        item {
            Box(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = stringResource(Res.string.total_points, scores.values.sum()),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        items(criteria) { c ->
            var value by remember { mutableFloatStateOf(scores[c.id]?.toFloat() ?: 0f) }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = stringResource(Res.string.criteria_with_value, c.name, scores[c.id] ?: 0),
                    style = MaterialTheme.typography.titleSmall,
                )
                Slider(
                    value = value,
                    enabled = !isArchived,
                    onValueChange = { value = it },
                    onValueChangeFinished = {
                        value = value.roundToInt().toFloat()
                        onUpdateScore(c.id, value.toInt())
                    },
                    valueRange = 0f..c.maxScore.toFloat(),
                    steps = c.maxScore - 1,
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }
    }
}