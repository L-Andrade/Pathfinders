package com.andradel.pathfinders.features.participant.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.R
import com.andradel.pathfinders.features.navArgs
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.model.ScoutClass
import com.andradel.pathfinders.model.participant.NewParticipant
import com.andradel.pathfinders.model.participant.OptionalParticipantArg
import com.andradel.pathfinders.validation.EmailValidation
import com.andradel.pathfinders.validation.NameValidation
import com.andradel.pathfinders.validation.ValidationResult
import com.andradel.pathfinders.validation.isValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditParticipantViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dataSource: ParticipantFirebaseDataSource,
    private val nameValidation: NameValidation,
    private val emailValidation: EmailValidation,
) : ViewModel() {
    private val participant = handle.navArgs<OptionalParticipantArg>().participant
    private val name = MutableStateFlow(participant?.name.orEmpty())
    private val email = MutableStateFlow(participant?.email.orEmpty())
    private val participantResult = MutableStateFlow<ParticipantResult?>(null)
    private val scoutClass = MutableStateFlow(participant?.scoutClass)

    val isEditing = participant != null

    val state: StateFlow<AddEditParticipantState> =
        combine(name, email, scoutClass, participantResult) { name, email, scoutClass, addParticipantResult ->
            val nameResult = nameValidation.validate(name)
            val emailResult = emailValidation.validate(email)
            AddEditParticipantState(
                name = name,
                email = email,
                nameValidation = nameResult,
                emailValidation = emailResult,
                scoutClass = scoutClass,
                isValid = nameResult.isValid && emailResult.isValid && scoutClass != null,
                participantResult = addParticipantResult,
                canDoInvestiture = isEditing && scoutClass != ScoutClass.Embaixador && scoutClass == participant?.scoutClass,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AddEditParticipantState(
                name = "",
                email = "",
                nameValidation = nameValidation.validate(""),
                emailValidation = ValidationResult.Valid,
                isValid = false,
                scoutClass = null,
                participantResult = null,
                canDoInvestiture = false,
            )
        )

    fun updateName(name: String) {
        this.name.value = name
    }

    fun updateEmail(email: String) {
        this.email.value = email
    }

    fun updateScoutClass(scoutClass: ScoutClass) {
        this.scoutClass.value = scoutClass
    }

    fun onInvestiture() {
        val classes = ScoutClass.entries
        this.scoutClass.value = classes[((scoutClass.value?.ordinal ?: 0) + 1).coerceAtMost(classes.lastIndex)]
    }

    fun addParticipant() {
        viewModelScope.launch {
            val scoutClass = state.value.scoutClass ?: ScoutClass.Invalid
            val email = state.value.email

            val emailAlreadyExists = dataSource.isParticipantEmailRegistered(email, participant?.id)
            if (emailAlreadyExists) {
                participantResult.value = ParticipantResult.Failure(R.string.email_already_exists)
            } else {
                val p = NewParticipant(
                    name = state.value.name,
                    email = email.takeIf { it.isNotBlank() },
                    scoutClass = scoutClass
                )
                dataSource.addOrUpdateParticipant(p, participant?.id)
                participantResult.value = ParticipantResult.Success
            }
        }
    }

    fun deleteParticipant() {
        if (participant != null) {
            viewModelScope.launch {
                dataSource.deleteParticipant(participant.id)
            }
        }
    }
}