package com.andradel.pathfinders.features.participant.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.R
import com.andradel.pathfinders.extensions.combine
import com.andradel.pathfinders.extensions.toMillis
import com.andradel.pathfinders.features.navArgs
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.participant.NewParticipant
import com.andradel.pathfinders.model.participant.OptionalParticipantArg
import com.andradel.pathfinders.user.UserRole
import com.andradel.pathfinders.user.UserSession
import com.andradel.pathfinders.user.role
import com.andradel.pathfinders.validation.EmailValidation
import com.andradel.pathfinders.validation.NameValidation
import com.andradel.pathfinders.validation.ValidationResult
import com.andradel.pathfinders.validation.isValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddEditParticipantViewModel @Inject constructor(
    handle: SavedStateHandle,
    userSession: UserSession,
    private val dataSource: ParticipantFirebaseDataSource,
    private val nameValidation: NameValidation,
    private val emailValidation: EmailValidation,
) : ViewModel() {
    private val participant = handle.navArgs<OptionalParticipantArg>().participant
    private val name = MutableStateFlow(participant?.name.orEmpty())
    private val email = MutableStateFlow(participant?.email.orEmpty())
    private val contact = MutableStateFlow(participant?.contact.orEmpty())
    private val birthday = MutableStateFlow(participant?.dateOfBirth)
    private val participantResult = MutableStateFlow<ParticipantResult?>(null)
    private val participantClass = MutableStateFlow(participant?.participantClass)

    val isEditing = participant != null

    val state: StateFlow<AddEditParticipantState> = combine(
        name, email, contact, participantClass, participantResult, birthday, userSession.role
    ) { name, email, contact, participantClass, result, birthday, role ->
        val nameResult = nameValidation.validate(name)
        val emailResult = emailValidation.validate(email)
        AddEditParticipantState(
            name = name,
            email = email,
            contact = contact,
            birthdayRepresentation = birthday?.toString(),
            birthday = (birthday ?: LocalDate.now()).atStartOfDay().toMillis(),
            nameValidation = nameResult,
            emailValidation = emailResult,
            // Always valid contact for now. Not sure if we want to validate this field at the moment
            contactValidation = ValidationResult.Valid,
            participantClass = participantClass,
            classOptions = (if (role is UserRole.ClassAdmin) role.classes else ParticipantClass.options).toList(),
            isValid = nameResult.isValid && emailResult.isValid && participantClass != null &&
                    result !is ParticipantResult.Loading,
            participantResult = result,
            canDoInvestiture = isEditing && participantClass != ParticipantClass.last && participantClass == participant?.participantClass,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AddEditParticipantState(
            name = "",
            email = "",
            contact = "",
            birthdayRepresentation = null,
            birthday = 0L,
            nameValidation = nameValidation.validate(""),
            emailValidation = ValidationResult.Valid,
            contactValidation = ValidationResult.Valid,
            isValid = false,
            participantClass = null,
            classOptions = emptyList(),
            participantResult = null,
            canDoInvestiture = false,
        )
    )

    fun updateName(name: String) {
        this.name.value = name
    }

    fun updateDate(millis: Long) {
        birthday.value = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun updateEmail(email: String) {
        this.email.value = email
    }

    fun updateContact(contact: String) {
        this.contact.value = contact
    }

    fun updateScoutClass(participantClass: ParticipantClass) {
        this.participantClass.value = participantClass
    }

    fun onInvestiture() {
        val classes = ParticipantClass.entries
        this.participantClass.value =
            classes[((participantClass.value?.ordinal ?: 0) + 1).coerceAtMost(classes.lastIndex)]
    }

    fun addParticipant() {
        viewModelScope.launch {
            val participantClass = state.value.participantClass ?: ParticipantClass.Invalid
            val email = state.value.email

            val emailAlreadyExists = dataSource.isParticipantEmailRegistered(email, participant?.id)
            if (emailAlreadyExists) {
                participantResult.value = ParticipantResult.Failure(R.string.email_already_exists)
            } else {
                val p = NewParticipant(
                    name = state.value.name,
                    email = email.takeIf { it.isNotBlank() },
                    contact = contact.value.takeIf { it.isNotBlank() },
                    participantClass = participantClass,
                    birthday = birthday.value?.toString()
                )
                participantResult.value = ParticipantResult.Loading
                dataSource.addOrUpdateParticipant(p, participant?.id).onSuccess {
                    participantResult.value = ParticipantResult.Success
                }.onFailure {
                    participantResult.value = ParticipantResult.Failure(R.string.generic_error)
                }
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