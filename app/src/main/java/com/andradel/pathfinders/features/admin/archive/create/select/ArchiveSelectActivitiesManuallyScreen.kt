package com.andradel.pathfinders.features.admin.archive.create.select

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.andradel.pathfinders.R
import com.andradel.pathfinders.extensions.collectChannelFlow
import com.andradel.pathfinders.model.title
import com.andradel.pathfinders.nav.NavigationRoute
import com.andradel.pathfinders.nav.navigateBackWithResult
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArchiveSelectActivitiesManuallyScreen(
    navigator: NavController,
    viewModel: ArchiveSelectActivitiesManuallyViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.collectChannelFlow(viewModel.result) { result ->
            navigator.navigateBackWithResult(NavigationRoute.ArchiveSelectActivitiesManually.Result, result)
        }
    }
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                title = stringResource(id = R.string.select),
                onIconClick = navigator::navigateUp,
            )
        },
    ) { padding ->
        val state by viewModel.state.collectAsStateWithLifecycle()
        when (val s = state) {
            ArchiveSelectActivitiesManuallyState.Loading -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ArchiveSelectActivitiesManuallyState.Selection -> Column(modifier = Modifier.padding(padding)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(s.activities, key = { it.id }) { activity ->
                        SelectableActivityItem(
                            item = activity,
                            onClick = { viewModel.select(activity.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                }
                HorizontalDivider()
                Button(
                    onClick = viewModel::onSelectActivities,
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                        .align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.select_number_of_activities, count = s.selected, s.selected,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectableActivityItem(item: SelectableActivity, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        border = BorderStroke(
            1.dp,
            color = if (item.selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier,
    ) {
        Column(Modifier.padding(all = 8.dp)) {
            Row {
                Text(text = item.name, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                if (item.selected) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(1.dp, MaterialTheme.colorScheme.primary, shape = CircleShape),
                    )
                }
            }
            Text(text = item.classes.map { it.title }.joinToString(", "), style = MaterialTheme.typography.bodySmall)
            if (item.date != null) {
                Text(text = item.date, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}