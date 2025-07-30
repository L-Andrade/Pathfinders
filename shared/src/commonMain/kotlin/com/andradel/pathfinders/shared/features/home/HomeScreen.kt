package com.andradel.pathfinders.shared.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.andradel.pathfinders.shared.auth.rememberAuthUiHandler
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.user.UserRole
import com.andradel.pathfinders.shared.user.isClassAdmin
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.activity_list
import pathfinders.shared.generated.resources.admin_screen
import pathfinders.shared.generated.resources.generic_error
import pathfinders.shared.generated.resources.ic_chevron_right
import pathfinders.shared.generated.resources.ic_warning
import pathfinders.shared.generated.resources.participant_list
import pathfinders.shared.generated.resources.reminders_screen
import pathfinders.shared.generated.resources.sign_in
import pathfinders.shared.generated.resources.sign_out
import pathfinders.shared.generated.resources.try_again
import pathfinders.shared.generated.resources.user_participant_profile

@Composable
fun HomeScreen(navigator: NavController, viewModel: HomeViewModel = koinViewModel()) {
    val authUiHandler = rememberAuthUiHandler { viewModel.onSignInResult() }
    val state by viewModel.state.collectAsState()
    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(all = 20.dp),
            ) {
                PathfindersImage(
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .padding(vertical = 20.dp)
                        .size(150.dp),
                )
                when (val s = state) {
                    HomeState.Guest -> GuestScreen(onSignInClick = { authUiHandler.onSignInClick() })
                    HomeState.Loading -> Loading()
                    is HomeState.Loaded -> LoggedInScreen(
                        state = s,
                        onSignOutClick = viewModel::onSignOutClick,
                        onParticipantsClick = { navigator.navigate(NavigationRoute.ParticipantList()) },
                        onAdminClick = { navigator.navigate(NavigationRoute.Admin) },
                        onActivitiesClick = { navigator.navigate(NavigationRoute.ActivityList()) },
                        onRemindersClick = { navigator.navigate(NavigationRoute.Reminders) },
                        onProfileClick = { navigator.navigate(NavigationRoute.ParticipantProfile(it)) },
                    )

                    HomeState.Error -> ErrorScreen(viewModel::updateUser)
                }
            }
        },
    )
}

@Composable
private fun ErrorScreen(retry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = stringResource(Res.string.generic_error), style = MaterialTheme.typography.titleSmall)
        Button(onClick = retry) {
            Text(text = stringResource(Res.string.try_again))
        }
    }
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
        // TODO: Get logo
        painter = painterResource(Res.drawable.ic_warning),
        modifier = modifier,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        alignment = Alignment.TopCenter,
    )
}

@Composable
private fun ListButton(onClick: () -> Unit, text: StringResource, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(vertical = 8.dp),
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(text),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(all = 24.dp)
                    .weight(1f),
            )
            Icon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                modifier = Modifier.padding(horizontal = 12.dp),
                contentDescription = stringResource(text),
            )
        }
    }
}

@Composable
private fun GuestScreen(onSignInClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = CenterHorizontally, modifier = modifier) {
        ListButton(onClick = onSignInClick, text = Res.string.sign_in)
    }
}

@Composable
private fun LoggedInScreen(
    state: HomeState.Loaded,
    onSignOutClick: () -> Unit,
    onParticipantsClick: () -> Unit,
    onAdminClick: () -> Unit,
    onActivitiesClick: () -> Unit,
    onRemindersClick: () -> Unit,
    onProfileClick: (Participant) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = CenterHorizontally, modifier = modifier) {
        if (state.user.role is UserRole.Admin) {
            ListButton(onParticipantsClick, Res.string.participant_list)
            ListButton(onAdminClick, Res.string.admin_screen)
        }
        if (state.user.role.isClassAdmin) {
            ListButton(onActivitiesClick, Res.string.activity_list)
            ListButton(onRemindersClick, Res.string.reminders_screen)
        }
        if (state.participant != null) {
            ListButton(
                onClick = { onProfileClick(state.participant) },
                text = Res.string.user_participant_profile,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        ListButton(onClick = onSignOutClick, text = Res.string.sign_out, modifier = Modifier.fillMaxWidth())
    }
}