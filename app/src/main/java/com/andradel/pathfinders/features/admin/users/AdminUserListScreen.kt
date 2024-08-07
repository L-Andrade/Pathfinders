package com.andradel.pathfinders.features.admin.users

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andradel.pathfinders.R
import com.andradel.pathfinders.features.destinations.EditUserRoleScreenDestination
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.user.User
import com.andradel.pathfinders.user.UserRole
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun AdminUserListScreen(navigator: DestinationsNavigator, viewModel: AdminUserListViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(titleRes = R.string.admin_user_list, onIconClick = navigator::navigateUp)
        },
        content = { padding ->
            val state by viewModel.state.collectAsState()
            LaunchedEffect(key1 = Unit) {
                viewModel.loadUsers()
            }
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                when (val s = state) {
                    AdminUserListScreenState.Error -> ErrorScreen(
                        viewModel::loadUsers,
                        modifier = Modifier.align(Alignment.Center),
                    )

                    is AdminUserListScreenState.Loaded ->
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                            items(s.users, key = { it.email.orEmpty() }) { user ->
                                UserItem(
                                    user = user,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .clickable { navigator.navigate(EditUserRoleScreenDestination(user)) },
                                )
                            }
                        }

                    AdminUserListScreenState.Loading ->
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        },
    )
}

@Composable
private fun UserItem(user: User, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name, style = MaterialTheme.typography.bodyMedium)
                Text(text = user.email.orEmpty(), style = MaterialTheme.typography.bodySmall)
            }
            Text(text = stringResource(id = user.role.stringRes), style = MaterialTheme.typography.labelMedium)
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ErrorScreen(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = stringResource(R.string.generic_error))
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.try_again))
        }
    }
}

private val UserRole.stringRes: Int
    @StringRes get() = when (this) {
        UserRole.Admin -> R.string.admin_role
        is UserRole.ClassAdmin -> R.string.class_admin_role
        UserRole.User -> R.string.user_role
    }