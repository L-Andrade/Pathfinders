package com.andradel.pathfinders.features.activity.add.criteria

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.andradel.pathfinders.R
import com.andradel.pathfinders.model.criteria.ActivityCriteria
import com.andradel.pathfinders.model.criteria.CriteriaSelectionArg
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination(navArgsDelegate = CriteriaSelectionArg::class)
fun AddCriteriaToActivityScreen(
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<CriteriaSelectionArg>,
    viewModel: AddCriteriaToActivityViewModel = koinViewModel(),
) {
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = R.string.select_criteria_for_activity,
                onIconClick = { navigator.navigateUp() },
            )
        },
    ) { padding ->
        val state by viewModel.state.collectAsState()
        Box(modifier = Modifier.padding(padding)) {
            when (val s = state) {
                is AddCriteriaToActivityState.Loaded -> CriteriaSelectionList(
                    s,
                    viewModel::selectCriteria,
                    viewModel::unselectCriteria,
                    viewModel::addCriteria,
                    onButtonClick = {
                        resultNavigator.setResult(CriteriaSelectionArg(ArrayList(s.selection)))
                        resultNavigator.navigateBack()
                    },
                )
                is AddCriteriaToActivityState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun CriteriaSelectionList(
    state: AddCriteriaToActivityState.Loaded,
    onSelectCriteria: (ActivityCriteria) -> Unit,
    onUnselectCriteria: (ActivityCriteria) -> Unit,
    onAddCriteria: (String, Int) -> Unit,
    onButtonClick: () -> Unit,
) {
    LazyColumn {
        item {
            AddCriteria(onAddCriteria, modifier = Modifier.padding(vertical = 16.dp))
        }
        if (state.selection.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.criteria_selection),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                )
            }
            items(state.selection) { criteria ->
                Criteria(
                    criteria = criteria,
                    selected = true,
                    onClick = { onUnselectCriteria(criteria) },
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
        item {
            Text(
                text = stringResource(id = R.string.criteria),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        if (state.criteria.isEmpty()) {
            item {
                NoMoreCriteria(modifier = Modifier.padding(vertical = 16.dp))
            }
        } else {
            items(state.criteria, key = { it.id }) { criteria ->
                Criteria(
                    criteria = criteria,
                    selected = false,
                    onClick = { onSelectCriteria(criteria) },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        }
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onButtonClick, modifier = Modifier.align(Alignment.Center)) {
                    Text(text = stringResource(id = R.string.select_number_of_criteria, state.selection.size))
                }
            }
        }
    }
}

@Composable
private fun AddCriteria(onAddCriteria: (String, Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        var criteriaName by remember { mutableStateOf("") }
        var maxScore by remember { mutableStateOf<Int?>(3) }
        Text(
            text = stringResource(id = R.string.add_criteria),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = criteriaName,
                onValueChange = { criteriaName = it },
                label = {
                    Text(text = stringResource(id = R.string.criteria_hint))
                },
                singleLine = true,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f),
            )
            TextField(
                value = maxScore?.toString().orEmpty(),
                onValueChange = { maxScore = it.toIntOrNull() },
                label = {
                    Text(text = stringResource(id = R.string.criteria_max_score))
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f),
            )
            val isValid by remember { derivedStateOf { criteriaName.length > 2 && (maxScore ?: 3) >= 1 } }
            OutlinedButton(
                enabled = isValid,
                onClick = {
                    onAddCriteria(criteriaName, maxScore ?: 3)
                    criteriaName = ""
                },
            ) {
                Text(text = stringResource(id = R.string.add))
            }
        }
    }
}

@Composable
private fun Criteria(
    criteria: ActivityCriteria,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = criteria.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Checkbox(checked = selected, onCheckedChange = { onClick() })
    }
}

@Composable
private fun NoMoreCriteria(modifier: Modifier) {
    Text(
        text = stringResource(id = R.string.no_more_criteria),
        modifier = modifier.padding(horizontal = 16.dp),
    )
}