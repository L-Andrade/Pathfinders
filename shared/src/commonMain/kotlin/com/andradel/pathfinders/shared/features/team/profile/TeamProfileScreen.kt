package com.andradel.pathfinders.shared.features.team.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andradel.pathfinders.shared.features.team.TeamInfo
import com.andradel.pathfinders.shared.model.team.Team
import com.andradel.pathfinders.shared.nav.Navigator
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.shared.ui.theme.Typography
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.activity_number
import pathfinders.shared.generated.resources.ic_chevron_down
import pathfinders.shared.generated.resources.participant_score
import pathfinders.shared.generated.resources.total
import pathfinders.shared.generated.resources.total_points
import pathfinders.shared.generated.resources.unknown_participant

@Composable
fun TeamProfileScreen(
    team: Team,
    archiveName: String?,
    navigator: Navigator,
    viewModel: TeamProfileViewModel = koinViewModel { parametersOf(team, archiveName) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { TopAppBarTitleWithIcon(title = state.title, onIconClick = { navigator.goBack() }) },
        content = { padding ->
            when (val s = state) {
                is TeamProfileState.Loaded -> TeamProfile(s, modifier = Modifier.padding(padding))
                is TeamProfileState.Loading -> Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        },
    )
}

@Composable
private fun TeamProfile(state: TeamProfileState.Loaded, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val pagerState = rememberPagerState { TeamProfileTab.entries.size }
        TeamInfo(
            participants = state.teamParticipants.size,
            points = state.points,
            activities = state.teamActivities.size,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        TeamProfileTabs(current = pagerState.currentPage) {
            val scope = rememberCoroutineScope()
            TeamProfileTab.entries.fastForEachIndexed { index, tab ->
                Tab(
                    text = { Text(tab.title) },
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                )
            }
        }
        HorizontalPager(state = pagerState) { page ->
            val tab = remember(page) { TeamProfileTab.entries[page] }
            when (tab) {
                TeamProfileTab.Participants -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.teamParticipants, key = { it.id }) { participant ->
                        TeamParticipant(participant)
                    }
                }

                TeamProfileTab.Activities -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.teamActivities, key = { it.id }) { activity ->
                        TeamActivity(activity)
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamProfileTabs(current: Int, modifier: Modifier = Modifier, tabs: @Composable () -> Unit) {
    PrimaryScrollableTabRow(
        selectedTabIndex = current,
        indicator = { TabRowDefaults.PrimaryIndicator(modifier = Modifier.tabIndicatorOffset(current)) },
        contentColor = MaterialTheme.colorScheme.onBackground,
        edgePadding = 0.dp,
        modifier = modifier,
        tabs = tabs,
    )
}

@Composable
private fun TeamParticipant(participant: TeamParticipant, modifier: Modifier = Modifier) {
    var showActivities by rememberSaveable { mutableStateOf(false) }
    val showDetails = remember { participant.activities.isNotEmpty() }
    Column(
        modifier = modifier
            .clickable(showDetails) { showActivities = !showActivities }
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = participant.name,
                style = Typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            if (showDetails) {
                Spacer(modifier = Modifier.width(8.dp))
                val rotation by animateFloatAsState(targetValue = if (showActivities) 180f else 0f)
                Icon(
                    painter = painterResource(Res.drawable.ic_chevron_down),
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(showActivities) {
            Column {
                participant.activities.fastForEach { activity ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                        Text(text = activity.name, style = Typography.bodyMedium, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.participant_score, activity.points),
                            style = Typography.labelSmall,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.activity_number, participant.activities.size),
                style = Typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(Res.string.total_points, participant.points),
                style = Typography.labelMedium,
            )
        }
    }
}

@Composable
private fun TeamActivity(activity: TeamActivity, modifier: Modifier = Modifier) {
    var showParticipants by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .clickable { showParticipants = !showParticipants }
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = activity.name, style = Typography.titleMedium, modifier = Modifier.weight(1f))
            if (activity.date != null) {
                Text(
                    text = activity.date,
                    style = Typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
            val rotation by animateFloatAsState(targetValue = if (showParticipants) 180f else 0f)
            Icon(
                painter = painterResource(Res.drawable.ic_chevron_down),
                contentDescription = null,
                modifier = Modifier.rotate(rotation),
            )
        }
        AnimatedVisibility(showParticipants) {
            Column {
                activity.participants.fastForEach { participant ->
                    TeamActivityParticipant(participant)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(vertical = 8.dp)) {
            Text(
                text = stringResource(Res.string.total),
                style = Typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = activity.total.toString(), style = Typography.labelMedium)
        }
    }
}

@Composable
private fun TeamActivityParticipant(participant: TeamActivityParticipant, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = participant.name?.let { AnnotatedString(it) } ?: buildAnnotatedString {
                withStyle(SpanStyle(fontStyle = Italic)) {
                    append(stringResource(Res.string.unknown_participant))
                }
            },
            style = Typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = participant.points.toString(), style = Typography.labelSmall)
    }
}