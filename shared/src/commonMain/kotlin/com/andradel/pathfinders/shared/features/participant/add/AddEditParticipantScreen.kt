package com.andradel.pathfinders.shared.features.participant.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.andradel.pathfinders.shared.model.ParticipantClass
import com.andradel.pathfinders.shared.model.color
import com.andradel.pathfinders.shared.model.title
import com.andradel.pathfinders.shared.ui.ConfirmationDialog
import com.andradel.pathfinders.shared.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.shared.ui.fields.DatePickerField
import com.andradel.pathfinders.shared.validation.errorMessage
import com.andradel.pathfinders.shared.validation.isError
import io.github.alexzhirkevich.compottie.ExperimentalCompottieApi
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.dynamic.rememberLottieDynamicProperties
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.add_participant
import pathfinders.shared.generated.resources.birthday_date
import pathfinders.shared.generated.resources.choose_class
import pathfinders.shared.generated.resources.contact_hint
import pathfinders.shared.generated.resources.delete
import pathfinders.shared.generated.resources.delete_confirmation
import pathfinders.shared.generated.resources.edit_participant
import pathfinders.shared.generated.resources.email_hint
import pathfinders.shared.generated.resources.ic_delete
import pathfinders.shared.generated.resources.ic_save
import pathfinders.shared.generated.resources.name_hint
import pathfinders.shared.generated.resources.participant_investiture
import pathfinders.shared.generated.resources.save

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddEditParticipantScreen(navigator: NavController, viewModel: AddEditParticipantViewModel = koinViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsState()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarTitleWithIcon(
                title = if (viewModel.isEditing) state.name else stringResource(Res.string.add_participant),
                onIconClick = { navigator.navigateUp() },
                endContent = {
                    IconButton(enabled = state.isValid, onClick = viewModel::addParticipant) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_save),
                            contentDescription = stringResource(Res.string.save),
                        )
                    }
                    DeleteParticipantIcon(viewModel.isEditing, state.name) {
                        viewModel.deleteParticipant()
                        navigator.navigateUp()
                    }
                },
            )
        },
        content = { padding ->
            val result by remember { derivedStateOf { state.participantResult } }
            var showCandle by remember { mutableStateOf(false) }
            var loading by remember { mutableStateOf(false) }
            AddParticipantResult(result, navigator, { loading = it }) {
                LaunchedEffect(key1 = result) {
                    snackbarHostState.showSnackbar(it)
                }
            }
            Box(modifier = Modifier.padding(padding)) {
                val alpha by animateFloatAsState(targetValue = if (showCandle) 0.2f else 1f, label = "CandleAlpha")
                AddEditForm(
                    state = state,
                    isEditing = viewModel.isEditing,
                    onInvestiture = {
                        showCandle = true
                        viewModel.onInvestiture()
                    },
                    addParticipant = viewModel::addParticipant,
                    updateEmail = viewModel::updateEmail,
                    updateName = viewModel::updateName,
                    updateContact = viewModel::updateContact,
                    updateScoutClass = viewModel::updateScoutClass,
                    updateDate = viewModel::updateDate,
                    modifier = Modifier.alpha(alpha),
                )
                AnimatedVisibility(showCandle, modifier = Modifier.align(Alignment.Center)) {
                    CandleAnimation(
                        start = !transition.isRunning,
                        color = state.participantClass?.color,
                        onEnd = viewModel::addParticipant,
                        modifier = Modifier.size(60.dp),
                    )
                }
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        },
    )
}

@Composable
@OptIn(ExperimentalCompottieApi::class)
private fun CandleAnimation(start: Boolean, color: Color?, onEnd: () -> Unit, modifier: Modifier = Modifier) {
    val color = color ?: MaterialTheme.colorScheme.primary
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/anim_candle.json").decodeToString())
    }
    val compState by animateLottieCompositionAsState(composition, isPlaying = start)
    // Dynamic properties are not changing colors correctly for some reason
    val dynamicProperties = rememberLottieDynamicProperties {
        layer("surface31887") {
            shapeLayer("surface31887") {
                fill("meltedCandleColor", "**") { color { color } }
                fill("candleColor", "**") { color { color } }
            }
        }
    }
    Image(
        painter = rememberLottiePainter(composition, progress = { compState }, dynamicProperties = dynamicProperties),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillWidth,
    )
    if (compState == 1f) {
        LaunchedEffect(key1 = compState) {
            onEnd()
        }
    }
}

@Composable
private fun AddEditForm(
    state: AddEditParticipantState,
    isEditing: Boolean,
    onInvestiture: () -> Unit,
    addParticipant: () -> Unit,
    updateEmail: (String) -> Unit,
    updateContact: (String) -> Unit,
    updateName: (String) -> Unit,
    updateDate: (Long) -> Unit,
    updateScoutClass: (ParticipantClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp),
    ) {
        TextField(
            value = state.name,
            onValueChange = updateName,
            label = {
                Text(state.nameValidation.errorMessage ?: stringResource(Res.string.name_hint))
            },
            isError = state.nameValidation.isError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.size(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ScoutClassDropDown(
                state.participantClass,
                state.classOptions,
                updateScoutClass,
                Modifier.weight(1f),
            )
            if (state.canDoInvestiture) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onInvestiture) {
                    Text(text = stringResource(Res.string.participant_investiture))
                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        TextField(
            value = state.email,
            onValueChange = updateEmail,
            label = {
                Text(state.emailValidation.errorMessage ?: stringResource(Res.string.email_hint))
            },
            isError = state.emailValidation.isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.size(24.dp))
        TextField(
            value = state.contact,
            onValueChange = updateContact,
            label = {
                Text(state.contactValidation.errorMessage ?: stringResource(Res.string.contact_hint))
            },
            isError = state.contactValidation.isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.size(24.dp))
        DatePickerField(
            dateRepresentation = state.birthdayRepresentation,
            dateMillis = state.birthday,
            updateDate = updateDate,
            hint = Res.string.birthday_date,
        )
        Spacer(modifier = Modifier.size(24.dp))
        Button(
            onClick = addParticipant,
            enabled = state.isValid,
            modifier = Modifier.align(Alignment.End),
        ) {
            val stringId = if (isEditing) Res.string.edit_participant else Res.string.add_participant
            Text(stringResource(stringId))
        }
    }
}

@Composable
private fun ScoutClassDropDown(
    currentClass: ParticipantClass?,
    options: List<ParticipantClass>,
    onClassChosen: (ParticipantClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dropdownState by remember { mutableStateOf(false) }
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .clickable { dropdownState = !dropdownState }
            .clip(RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp))
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(all = 16.dp),
    ) {
        Text(
            text = currentClass?.title ?: stringResource(Res.string.choose_class),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (currentClass == null) 0.5f else 1f),
        )
        DropdownMenu(
            expanded = dropdownState,
            onDismissRequest = { dropdownState = !dropdownState },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(0.5f)),
        ) {
            options.forEach { scoutClass ->
                DropdownMenuItem(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = if (scoutClass == currentClass) .5f else 0f),
                        ),
                    onClick = {
                        onClassChosen(scoutClass)
                        dropdownState = !dropdownState
                    },
                    text = {
                        Text(text = scoutClass.title)
                    },
                )
            }
        }
    }
}

@Composable
private fun AddParticipantResult(
    result: ParticipantResult?,
    navigator: NavController,
    onLoading: (Boolean) -> Unit,
    showSnackbar: @Composable (String) -> Unit,
) {
    onLoading(result is ParticipantResult.Loading)
    when (result) {
        is ParticipantResult.Failure -> showSnackbar(stringResource(result.message))
        ParticipantResult.Success -> navigator.navigateUp()
        ParticipantResult.Loading, null -> Unit
    }
}

@Composable
private fun DeleteParticipantIcon(isEditing: Boolean, name: String, onDelete: () -> Unit) {
    if (isEditing) {
        var deleteDialog by remember { mutableStateOf(false) }
        IconButton(onClick = { deleteDialog = true }) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = stringResource(Res.string.delete),
            )
        }
        if (deleteDialog) {
            ConfirmationDialog(
                onDismiss = { deleteDialog = false },
                onConfirm = onDelete,
                title = stringResource(Res.string.delete),
                body = stringResource(Res.string.delete_confirmation, name),
            )
        }
    }
}