package com.andradel.pathfinders.features.participant.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.andradel.pathfinders.R
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.activity.participantPoints
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import org.koin.androidx.compose.koinViewModel

@Composable
fun ParticipantProfileScreen(
    navigator: NavController,
    viewModel: ParticipantProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                title = state.participant.name,
                onIconClick = { navigator.navigateUp() },
            )
        },
        content = { padding ->
            when (val s = state) {
                is ParticipantProfileState.Loaded -> LoadedProfile(s, modifier = Modifier.padding(padding))
                is ParticipantProfileState.Loading -> Box(
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
private fun LoadedProfile(state: ParticipantProfileState.Loaded, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(all = 20.dp)) {
        item {
            Text(
                text = stringResource(id = R.string.activity_list),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (state.activities.isNotEmpty()) {
            items(state.activities) { activity ->
                ParticipantActivity(state.participant, activity)
            }
        } else {
            item {
                Text(
                    stringResource(id = R.string.user_no_activities, state.participant.name),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

@Composable
private fun ParticipantActivity(participant: Participant, activity: Activity, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            val activityScore = remember { activity.participantPoints(participant.id) }
            Text(
                text = activity.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(id = R.string.participant_score, activityScore),
                style = MaterialTheme.typography.titleSmall,
            )
        }
        activity.criteria.fastForEach { criteria ->
            val criteriaScore = remember { activity.scores[participant.id]?.get(criteria.id) ?: 0 }
            Text(
                text = stringResource(id = R.string.criteria_with_value, criteria.name, criteriaScore),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
            )
        }
        HorizontalDivider(Modifier.padding(top = 8.dp))
    }
}