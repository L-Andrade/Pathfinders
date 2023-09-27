package com.andradel.pathfinders.features.participant.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.andradel.pathfinders.R
import com.andradel.pathfinders.model.ScoutClass
import com.andradel.pathfinders.model.color
import com.andradel.pathfinders.model.participant.OptionalParticipantArg
import com.andradel.pathfinders.model.title
import com.andradel.pathfinders.ui.ConfirmationDialog
import com.andradel.pathfinders.ui.TopAppBarTitleWithIcon
import com.andradel.pathfinders.validation.errorMessage
import com.andradel.pathfinders.validation.isError
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Destination(navArgsDelegate = OptionalParticipantArg::class)
fun AddEditParticipantScreen(
    navigator: DestinationsNavigator,
    viewModel: AddEditParticipantViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBarTitleWithIcon(
                title = if (viewModel.isEditing) state.name else stringResource(id = R.string.add_participant),
                onIconClick = { navigator.navigateUp() },
                endContent = {
                    IconButton(onClick = viewModel::addParticipant) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_save),
                            contentDescription = stringResource(id = R.string.save)
                        )
                    }
                    DeleteParticipantIcon(viewModel.isEditing, state.name) {
                        viewModel.deleteParticipant()
                        navigator.navigateUp()
                    }
                }
            )
        },
        scaffoldState = scaffoldState,
        content = { padding ->
            val result by remember { derivedStateOf { state.participantResult } }
            var showCandle by remember { mutableStateOf(false) }
            AddParticipantResult(result, navigator) {
                LaunchedEffect(key1 = result) {
                    scaffoldState.snackbarHostState.showSnackbar(it)
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
                    updateScoutClass = viewModel::updateScoutClass,
                    modifier = Modifier.alpha(alpha)
                )
                AnimatedVisibility(showCandle, modifier = Modifier.align(Alignment.Center)) {
                    CandleAnimation(
                        start = !transition.isRunning,
                        color = state.scoutClass?.color,
                        onEnd = viewModel::addParticipant,
                    )
                }
            }
        }
    )
}

@Composable
private fun CandleAnimation(start: Boolean, color: Color?, onEnd: () -> Unit, modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_candle))
    val compState by animateLottieCompositionAsState(composition, isPlaying = start)
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = SimpleColorFilter((color ?: MaterialTheme.colors.primary).toArgb()),
            keyPath = arrayOf("surface31887", "surface31887", "meltedCandleColor", "**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = SimpleColorFilter((color ?: MaterialTheme.colors.primary).toArgb()),
            keyPath = arrayOf("surface31887", "surface31887", "candleColor", "**")
        ),
    )
    LottieAnimation(
        composition,
        progress = { compState },
        contentScale = ContentScale.FillWidth,
        dynamicProperties = dynamicProperties,
        modifier = modifier
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
    updateName: (String) -> Unit,
    updateScoutClass: (ScoutClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        TextField(
            value = state.name,
            onValueChange = updateName,
            label = {
                Text(state.nameValidation.errorMessage ?: stringResource(id = R.string.name_hint))
            },
            isError = state.nameValidation.isError,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(24.dp))
        Row {
            ScoutClassDropDown(
                state.scoutClass,
                ScoutClass.options,
                updateScoutClass,
                Modifier.weight(1f)
            )
            if (state.canDoInvestiture) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onInvestiture) {
                    Text(text = stringResource(id = R.string.participant_investiture))
                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        TextField(
            value = state.email,
            onValueChange = updateEmail,
            label = {
                Text(state.emailValidation.errorMessage ?: stringResource(id = R.string.email_hint))
            },
            isError = state.emailValidation.isError,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(24.dp))
        Button(
            onClick = addParticipant,
            enabled = state.isValid,
            modifier = Modifier.align(Alignment.End)
        ) {
            val stringId = if (isEditing) R.string.edit_participant else R.string.add_participant
            Text(stringResource(id = stringId))
        }
    }
}

@Composable
private fun ScoutClassDropDown(
    currentClass: ScoutClass?,
    options: List<ScoutClass>,
    onClassChosen: (ScoutClass) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownState by remember { mutableStateOf(false) }
    val backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity)
    Box(
        modifier = modifier
            .clickable { dropdownState = !dropdownState }
            .clip(RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp))
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(all = 16.dp)
    ) {
        Text(
            text = currentClass?.title ?: stringResource(id = R.string.choose_class),
            color = MaterialTheme.colors.onBackground.copy(alpha = if (currentClass == null) 0.5f else 1f)
        )
        DropdownMenu(
            expanded = dropdownState,
            onDismissRequest = { dropdownState = !dropdownState },
            modifier = Modifier.background(MaterialTheme.colors.surface.copy(0.5f))
        ) {
            options.forEach { scoutClass ->
                DropdownMenuItem(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colors.surface.copy(alpha = if (scoutClass == currentClass) .5f else 0f)
                        ),
                    onClick = {
                        onClassChosen(scoutClass)
                        dropdownState = !dropdownState
                    }, content = {
                        Text(text = scoutClass.title)
                    }
                )
            }
        }
    }
}

@Composable
private fun AddParticipantResult(
    result: ParticipantResult?,
    navigator: DestinationsNavigator,
    showSnackbar: @Composable (String) -> Unit
) {
    when (result) {
        is ParticipantResult.Failure -> showSnackbar(stringResource(id = result.message))
        ParticipantResult.Success -> navigator.navigateUp()
        null -> Unit
    }
}

@Composable
private fun DeleteParticipantIcon(
    isEditing: Boolean,
    name: String,
    onDelete: () -> Unit,
) {
    if (isEditing) {
        var deleteDialog by remember { mutableStateOf(false) }
        IconButton(onClick = { deleteDialog = true }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = stringResource(id = R.string.delete)
            )
        }
        if (deleteDialog) {
            ConfirmationDialog(
                onDismiss = { deleteDialog = false },
                onConfirm = onDelete,
                title = stringResource(id = R.string.delete),
                body = stringResource(id = R.string.delete_confirmation, name)
            )
        }
    }
}