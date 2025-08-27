package com.andradel.pathfinders.shared.features.admin.users

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.shared.user.User
import com.andradel.pathfinders.shared.user.UserRole
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.admin_role
import pathfinders.shared.generated.resources.admin_user_list
import pathfinders.shared.generated.resources.class_admin_role
import pathfinders.shared.generated.resources.generic_error
import pathfinders.shared.generated.resources.ic_chevron_right
import pathfinders.shared.generated.resources.try_again
import pathfinders.shared.generated.resources.user_role

@Composable
fun AdminUserListScreen(navigator: NavController, viewModel: AdminUserListViewModel = koinViewModel()) {
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(titleRes = Res.string.admin_user_list, onIconClick = navigator::navigateUp)
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
                                        .clickable { navigator.navigate(NavigationRoute.EditUserRole(user)) },
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
            Text(text = stringResource(user.role.stringRes), style = MaterialTheme.typography.labelMedium)
            Icon(
                painter = painterResource(Res.drawable.ic_chevron_right),
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
        Text(text = stringResource(Res.string.generic_error))
        Button(onClick = onRetry) {
            Text(text = stringResource(Res.string.try_again))
        }
    }
}

private val UserRole.stringRes: StringResource
    get() = when (this) {
        UserRole.Admin -> Res.string.admin_role
        is UserRole.ClassAdmin -> Res.string.class_admin_role
        UserRole.User -> Res.string.user_role
    }