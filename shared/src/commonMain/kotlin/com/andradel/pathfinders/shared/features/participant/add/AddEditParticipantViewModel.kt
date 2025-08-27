package com.andradel.pathfinders.shared.features.participant.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andradel.pathfinders.flavors.model.ParticipantClass
import com.andradel.pathfinders.shared.extensions.combine
import com.andradel.pathfinders.shared.extensions.toLocalDate
import com.andradel.pathfinders.shared.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.shared.model.participant.NewParticipant
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.customNavType
import com.andradel.pathfinders.shared.user.UserRole
import com.andradel.pathfinders.shared.user.UserSession
import com.andradel.pathfinders.shared.user.role
import com.andradel.pathfinders.shared.validation.EmailValidation
import com.andradel.pathfinders.shared.validation.NameValidation
import com.andradel.pathfinders.shared.validation.ValidationResult
import com.andradel.pathfinders.shared.validation.isValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import org.koin.android.annotation.KoinViewModel
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.email_already_exists
import pathfinders.shared.generated.resources.generic_error
import kotlin.reflect.typeOf
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@KoinViewModel
class AddEditParticipantViewModel(
    handle: SavedStateHandle,
    userSession: UserSession,
    private val dataSource: ParticipantFirebaseDataSource,
    private val nameValidation: NameValidation,
    private val emailValidation: EmailValidation,
) : ViewModel() {
    private val participant = handle.toRoute<NavigationRoute.AddEditParticipant>(
        typeMap = mapOf(typeOf<Participant?>() to customNavType<Participant?>(isNullableAllowed = true)),
    ).participant
    private val name = MutableStateFlow(participant?.name.orEmpty())
    private val email = MutableStateFlow(participant?.email.orEmpty())
    private val contact = MutableStateFlow(participant?.contact.orEmpty())
    private val birthday = MutableStateFlow(participant?.dateOfBirth)
    private val participantResult = MutableStateFlow<ParticipantResult?>(null)
    private val participantClass = MutableStateFlow(participant?.participantClass)

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    val isEditing = participant != null

    val state: StateFlow<AddEditParticipantState> = combine(
        name, email, contact, participantClass, participantResult, birthday, userSession.role,
    ) { name, email, contact, participantClass, result, birthday, role ->
        val nameResult = nameValidation.validate(name)
        val emailResult = emailValidation.validate(email)
        AddEditParticipantState(
            name = name,
            email = email,
            contact = contact,
            birthdayRepresentation = birthday?.toString(),
            birthday = (birthday ?: today).atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            nameValidation = nameResult,
            emailValidation = emailResult,
            // Always valid contact for now. Not sure if we want to validate this field at the moment
            contactValidation = ValidationResult.Valid,
            participantClass = participantClass,
            classOptions = (if (role is UserRole.ClassAdmin) role.classes else ParticipantClass.options).toList(),
            isValid = nameResult.isValid && emailResult.isValid && participantClass != null &&
                result !is ParticipantResult.Loading,
            participantResult = result,
            canDoInvestiture = isEditing &&
                participantClass != ParticipantClass.last && participantClass == participant?.participantClass,
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
        ),
    )

    fun updateName(name: String) {
        this.name.value = name
    }

    fun updateDate(millis: Long) {
        birthday.value = millis.toLocalDate()
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
                participantResult.value = ParticipantResult.Failure(Res.string.email_already_exists)
            } else {
                val p = NewParticipant(
                    name = state.value.name,
                    email = email.takeIf { it.isNotBlank() },
                    contact = contact.value.takeIf { it.isNotBlank() },
                    participantClass = participantClass,
                    birthday = birthday.value?.toString(),
                )
                participantResult.value = ParticipantResult.Loading
                dataSource.addOrUpdateParticipant(p, participant?.id).onSuccess {
                    participantResult.value = ParticipantResult.Success
                }.onFailure {
                    participantResult.value = ParticipantResult.Failure(Res.string.generic_error)
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