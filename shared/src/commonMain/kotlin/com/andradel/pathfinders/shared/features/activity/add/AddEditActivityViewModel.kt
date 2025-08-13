package com.andradel.pathfinders.shared.features.activity.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andradel.pathfinders.shared.extensions.combine
import com.andradel.pathfinders.shared.extensions.toLocalDate
import com.andradel.pathfinders.shared.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.shared.model.ParticipantClass
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.model.activity.NewActivity
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.customNavType
import com.andradel.pathfinders.shared.user.UserSession
import com.andradel.pathfinders.shared.user.isAdmin
import com.andradel.pathfinders.shared.validation.NameValidation
import com.andradel.pathfinders.shared.validation.isValid
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.koin.android.annotation.KoinViewModel
import kotlin.reflect.typeOf
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@KoinViewModel
class AddEditActivityViewModel(
    handle: SavedStateHandle,
    userSession: UserSession,
    private val dataSource: ActivityFirebaseDataSource,
    private val nameValidation: NameValidation,
) : ViewModel() {
    private val route = handle.toRoute<NavigationRoute.AddEditActivity>(
        typeMap = mapOf(typeOf<Activity?>() to customNavType<Activity?>(isNullableAllowed = true)),
    )
    private var activity: Activity? = null
    private val isArchived = route.archiveName != null

    private val participants = MutableStateFlow(emptyList<Participant>())
    private val criteria = MutableStateFlow(emptyList<ActivityCriteria>())
    private val classes = MutableStateFlow(emptyList<ParticipantClass>())
    private val name = MutableStateFlow("")
    private val date = MutableStateFlow<LocalDate?>(null)
    private val activityResult = MutableStateFlow<ActivityResult?>(null)
    private val createForEach = MutableStateFlow(false)

    private val _error = Channel<Unit>()
    val error = _error.receiveAsFlow()

    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val state = combine(
        name,
        date,
        participants,
        classes,
        criteria,
        activityResult,
        userSession.isAdmin,
        createForEach,
    ) { name, date, participants, classes, criteria, activityResult, isAdmin, createForEach ->
        val nameValidation = nameValidation.validate(name)
        AddEditActivityState(
            name = name,
            nameValidation = nameValidation,
            dateRepresentation = date?.toString(),
            date = (date ?: today.date).atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            classes = classes,
            participants = participants,
            criteria = criteria,
            isValid = nameValidation.isValid && activityResult != ActivityResult.Loading && !isArchived,
            isAdmin = isAdmin && !isArchived,
            isArchived = isArchived,
            activityResult = activityResult,
            createForEach = createForEach,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddEditActivityState())

    val isEditing = route.activityId != null && !isArchived
    val isUnsaved: Boolean
        get() = activity?.toNewActivity() != state.value.toNewActivity()

    init {
        loadActivity()
    }

    fun loadActivity() {
        if (route.activityId != null) {
            viewModelScope.launch {
                dataSource.getActivity(route.activityId, route.archiveName).onSuccess { activity ->
                    this@AddEditActivityViewModel.activity = activity
                    participants.value = activity?.participants.orEmpty()
                    criteria.value = activity?.criteria.orEmpty()
                    classes.value = activity?.classes.orEmpty()
                    name.value = activity?.name.orEmpty()
                    date.value = activity?.date
                }.onFailure {
                    _error.send(Unit)
                }
            }
        }
    }

    fun addActivity() {
        activityResult.value = ActivityResult.Loading
        viewModelScope.launch {
            if (createForEach.value) {
                supervisorScope {
                    val deferrables = classes.value.map { pClass ->
                        async {
                            dataSource.addOrUpdateActivity(state.value.toNewActivity(listOf(pClass)), activityId = null)
                        }
                    }
                    deferrables.awaitAll()
                    activityResult.value = ActivityResult.Success
                }
            } else {
                dataSource.addOrUpdateActivity(state.value.toNewActivity(), activityId = route.activityId).onSuccess {
                    activityResult.value = ActivityResult.Success
                }.onFailure {
                    activityResult.value = ActivityResult.Failure
                }
            }
        }
    }

    private fun Activity.toNewActivity(): NewActivity = NewActivity(
        name = name,
        date = date?.toString(),
        participants = participants,
        classes = classes,
        criteria = criteria,
        scores = scores,
    )

    private fun AddEditActivityState.toNewActivity(classes: List<ParticipantClass> = this.classes): NewActivity =
        NewActivity(
            name = name,
            date = dateRepresentation,
            participants = participants,
            classes = classes,
            criteria = criteria,
            scores = activity?.scores.orEmpty(),
        )

    fun setSelection(participants: List<Participant>) {
        this.participants.value = participants
    }

    fun setCriteriaSelection(criteria: List<ActivityCriteria>) {
        this.criteria.value = criteria
    }

    fun setClassSelected(participantClass: ParticipantClass, selected: Boolean) {
        classes.value = if (selected) classes.value + participantClass else classes.value - participantClass
    }

    fun setAllSelected(selected: Boolean) {
        classes.value = if (selected) ParticipantClass.options else emptyList()
    }

    fun updateName(name: String) {
        this.name.value = name
    }

    fun updateDate(millis: Long) {
        date.value = millis.toLocalDate()
    }

    fun deleteActivity() {
        if (route.activityId != null) {
            activityResult.value = ActivityResult.Loading
            viewModelScope.launch {
                dataSource.deleteActivity(route.activityId).onSuccess {
                    activityResult.value = ActivityResult.Success
                }.onFailure {
                    activityResult.value = ActivityResult.Failure
                }
            }
        }
    }

    fun setCreateForEach(createForEach: Boolean) {
        this.createForEach.value = createForEach
    }
}