package com.andradel.pathfinders.features.participant.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andradel.pathfinders.R
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.activity.participantPoints
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.model.participant.ParticipantArg
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(navArgsDelegate = ParticipantArg::class)
fun ParticipantProfileScreen(
    navigator: DestinationsNavigator,
    viewModel: ParticipantProfileViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                title = state.participant.name,
                onIconClick = { navigator.navigateUp() },
            )
        },
        scaffoldState = scaffoldState,
        content = { padding ->
            when (val s = state) {
                is ParticipantProfileState.Loaded -> LoadedProfile(s, modifier = Modifier.padding(padding))
                is ParticipantProfileState.Loading -> Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    )
}

@Composable
private fun LoadedProfile(
    state: ParticipantProfileState.Loaded,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(all = 20.dp)) {
        item {
            Text(
                text = stringResource(id = R.string.activity_list),
                style = MaterialTheme.typography.h5,
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
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }
    }
}

@Composable
private fun ParticipantActivity(
    participant: Participant,
    activity: Activity,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            val activityScore = remember { activity.participantPoints(participant.id) }
            Text(
                text = activity.name,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(id = R.string.participant_score, activityScore),
                style = MaterialTheme.typography.subtitle2
            )
        }
        activity.criteria.forEach { criteria ->
            val criteriaScore = remember { activity.scores[participant.id]?.get(criteria.id) ?: 0 }
            Text(
                text = stringResource(id = R.string.criteria_with_value, criteria.name, criteriaScore),
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
            )
        }
        Divider(Modifier.padding(top = 8.dp))
    }
}
