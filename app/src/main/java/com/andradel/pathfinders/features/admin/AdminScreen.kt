package com.andradel.pathfinders.features.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andradel.pathfinders.R
import com.andradel.pathfinders.features.destinations.AdminUserListScreenDestination
import com.andradel.pathfinders.features.destinations.ArchiveListScreenDestination
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun AdminScreen(navigator: DestinationsNavigator) {
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(titleRes = R.string.admin_screen, onIconClick = navigator::navigateUp)
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                AdminOption(
                    text = stringResource(id = R.string.admin_user_list),
                    onClick = { navigator.navigate(AdminUserListScreenDestination) },
                )
                AdminOption(
                    text = stringResource(id = R.string.admin_archive),
                    onClick = { navigator.navigate(ArchiveListScreenDestination) },
                )
            }
        },
    )
}

@Composable
private fun AdminOption(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp), onClick = onClick) {
        Row(modifier = Modifier.padding(all = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Icon(painter = painterResource(id = R.drawable.ic_chevron_right), contentDescription = text)
        }
    }
}