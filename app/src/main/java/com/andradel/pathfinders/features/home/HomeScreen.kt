package com.andradel.pathfinders.features.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andradel.pathfinders.R
import com.andradel.pathfinders.features.destinations.ActivityListScreenDestination
import com.andradel.pathfinders.features.destinations.ParticipantListScreenDestination
import com.andradel.pathfinders.features.destinations.ParticipantProfileScreenDestination
import com.andradel.pathfinders.features.destinations.RemindersScreenDestination
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.user.UserRole
import com.andradel.pathfinders.user.isAdmin
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
@RootNavGraph(start = true)
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val resultLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract(),
        onResult = viewModel::onSignInResult
    )
    val state by viewModel.state.collectAsState()
    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(all = 20.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                PathfindersImage(
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .padding(vertical = 20.dp)
                        .size(150.dp)
                )
                when (val s = state) {
                    HomeState.Guest -> GuestScreen(
                        onSignInClick = { resultLauncher.launch(viewModel.signInIntent) },
                    )

                    HomeState.Loading -> Loading()
                    is HomeState.Loaded -> LoggedInScreen(
                        state = s,
                        onSignOutClick = viewModel::onSignOutClick,
                        onParticipantsClick = { navigator.navigate(ParticipantListScreenDestination) },
                        onActivitiesClick = { navigator.navigate(ActivityListScreenDestination) },
                        onRemindersClick = { navigator.navigate(RemindersScreenDestination) },
                        onProfileClick = { navigator.navigate(ParticipantProfileScreenDestination(it)) }
                    )
                }
            }
        }
    )
}

@Composable
private fun Loading() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PathfindersImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        modifier = modifier,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        alignment = Alignment.TopCenter
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListButton(onClick: () -> Unit, @StringRes text: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = text),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(all = 24.dp)
                    .weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                modifier = Modifier.padding(horizontal = 12.dp),
                contentDescription = stringResource(id = text)
            )
        }
    }
}

@Composable
private fun GuestScreen(
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = CenterHorizontally, modifier = modifier) {
        ListButton(onClick = onSignInClick, text = R.string.sign_in)
    }
}

@Composable
private fun LoggedInScreen(
    state: HomeState.Loaded,
    onSignOutClick: () -> Unit,
    onParticipantsClick: () -> Unit,
    onActivitiesClick: () -> Unit,
    onRemindersClick: () -> Unit,
    onProfileClick: (Participant) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = CenterHorizontally, modifier = modifier) {
        if (state.user.role is UserRole.Admin) {
            ListButton(onParticipantsClick, R.string.participant_list)
        }
        if (state.user.role.isAdmin) {
            ListButton(onActivitiesClick, R.string.activity_list)
            ListButton(onRemindersClick, R.string.reminders_screen)
        }
        if (state.participant != null) {
            ListButton(
                onClick = { onProfileClick(state.participant) },
                text = R.string.user_participant_profile,
                modifier = Modifier.fillMaxWidth()
            )
        }
        ListButton(onClick = onSignOutClick, text = R.string.sign_out, modifier = Modifier.fillMaxWidth())
    }
}