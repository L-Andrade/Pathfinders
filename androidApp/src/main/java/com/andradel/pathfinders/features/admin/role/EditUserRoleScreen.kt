package com.andradel.pathfinders.features.admin.role

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.andradel.pathfinders.R
import com.andradel.pathfinders.extensions.collectChannelFlow
import com.andradel.pathfinders.features.admin.role.model.EditUserRole
import com.andradel.pathfinders.features.admin.role.model.stringRes
import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.color
import com.andradel.pathfinders.model.title
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditUserRoleScreen(navigator: NavController, viewModel: EditUserRoleViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.collectChannelFlow(viewModel.result) { result ->
            result.onSuccess {
                navigator.navigateUp()
            }.onFailure {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = context.getString(R.string.generic_error),
                    actionLabel = context.getString(R.string.try_again),
                    duration = SnackbarDuration.Short,
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    viewModel.save()
                }
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarTitleWithIcon(
                titleRes = R.string.edit_user_role,
                onIconClick = navigator::navigateUp,
                endContent = {
                    IconButton(enabled = state.enabled, onClick = viewModel::save) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_save),
                            contentDescription = stringResource(id = R.string.save),
                        )
                    }
                },
            )
        },
        content = { padding ->
            Box {
                if (state.loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                EditUserRoleColumn(
                    state = state,
                    onSelectClass = viewModel::selectClass,
                    onSelectRole = viewModel::selectRole,
                    onSave = viewModel::save,
                    modifier = Modifier.padding(padding),
                )
            }
        },
    )
}

@Composable
private fun EditUserRoleColumn(
    state: EditUserRoleState,
    onSelectClass: (ParticipantClass) -> Unit,
    onSelectRole: (EditUserRole) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val roles = remember { EditUserRole.entries }
    val classes = remember { ParticipantClass.options }
    Column(modifier = modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = state.name, style = MaterialTheme.typography.titleMedium)
        RoleDropdown(state.role, roles, onSelectRole)
        AnimatedVisibility(visible = state.classes != null) {
            if (state.classes != null) {
                ClassesCheckboxes(
                    state = state,
                    availableClasses = classes,
                    onSelectClass = onSelectClass,
                )
            }
        }
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Button(enabled = state.enabled, onClick = onSave) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}

@Composable
private fun ClassesCheckboxes(
    state: EditUserRoleState,
    availableClasses: List<ParticipantClass>,
    onSelectClass: (ParticipantClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        availableClasses.forEach { pClass ->
            key(pClass) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pClass.title,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f),
                    )
                    val selected by remember(state.classes) { derivedStateOf { pClass in state.classes.orEmpty() } }
                    Checkbox(
                        checked = selected,
                        onCheckedChange = { onSelectClass(pClass) },
                        colors = CheckboxDefaults.colors(checkedColor = pClass.color),
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleDropdown(
    selectedRole: EditUserRole,
    roles: List<EditUserRole>,
    onSelectRole: (EditUserRole) -> Unit,
    modifier: Modifier = Modifier,
) {
    var roleExpanded by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.role),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f),
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.clickable { roleExpanded = !roleExpanded },
        ) {
            Text(
                text = stringResource(id = selectedRole.stringRes),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(all = 16.dp),
            )
            DropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }) {
                roles.forEach { role ->
                    key(role) {
                        DropdownMenuItem(
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.surface.copy(alpha = if (role == selectedRole) .5f else 0f),
                            ),
                            onClick = {
                                onSelectRole(role)
                                roleExpanded = false
                            },
                            text = {
                                Text(text = stringResource(id = role.stringRes))
                            },
                        )
                    }
                }
            }
        }
    }
}