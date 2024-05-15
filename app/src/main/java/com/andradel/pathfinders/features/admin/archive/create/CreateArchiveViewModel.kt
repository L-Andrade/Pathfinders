package com.andradel.pathfinders.features.admin.archive.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.extensions.toLocalDate
import com.andradel.pathfinders.firebase.activity.ActivityCriteriaFirebaseDataSource
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.firebase.archive.ArchiveFirebaseDataSource
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.criteria.ActivityCriteria
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.validation.NameValidation
import com.andradel.pathfinders.validation.isValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateArchiveViewModel @Inject constructor(
    private val dataSource: ArchiveFirebaseDataSource,
    private val activityDataSource: ActivityFirebaseDataSource,
    private val participantDataSource: ParticipantFirebaseDataSource,
    private val criteriaDataSource: ActivityCriteriaFirebaseDataSource,
    private val nameValidation: NameValidation,
) : ViewModel() {
    private val name = MutableStateFlow("")
    private val activities = MutableStateFlow<List<Activity>>(emptyList())
    private val deleteParticipants = MutableStateFlow(false)
    private val deleteCriteria = MutableStateFlow(false)

    private val allActivities = activityDataSource.activities(null)

    private val _result = Channel<Result<Unit>>()
    val result = _result.receiveAsFlow()

    private val _progressState = MutableStateFlow<CreateArchiveProgressState?>(null)
    val progressState = _progressState.asStateFlow()

    val state = combine(
        allActivities,
        name,
        activities,
        deleteParticipants,
        deleteCriteria
    ) { allActivities, name, activities, deleteParticipants, deleteCriteria ->
        val nameResult = nameValidation.validate(name)
        val affectedParticipants = activities.affectedParticipants.map { it.id }
        val affectedCriteria = activities.affectedCriteria.map { it.id }
        val selectedIds = activities.map { it.id }
        val otherActivities = allActivities.filter { it.id !in selectedIds }
        CreateArchiveState(
            name = name,
            nameValidation = nameResult,
            deleteParticipants = deleteParticipants,
            deleteCriteria = deleteCriteria,
            activities = activities,
            participants = Affected(
                affectedParticipants.size,
                otherActivities.flatMap { it.participants }
                    .filter { it.id in affectedParticipants }.distinctBy { it.id }.size,
            ).takeIf { it.size > 0 },
            criteria = Affected(
                affectedCriteria.size,
                otherActivities.flatMap { it.criteria }
                    .filter { it.id in affectedCriteria }.distinctBy { it.id }.size,
            ).takeIf { it.size > 0 },
            canSave = activities.isNotEmpty() && nameResult.isValid
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreateArchiveState())

    fun onUpdateName(value: String) {
        name.value = value
    }

    fun onCheckDeleteParticipants(value: Boolean) {
        deleteParticipants.value = value
    }

    fun onCheckDeleteCriteria(value: Boolean) {
        deleteCriteria.value = value
    }

    fun unselectActivity(activity: Activity) {
        activities.update { activities -> activities.filter { it.id != activity.id } }
    }

    fun selectAll() {
        viewModelScope.launch {
            activities.update { allActivities.first() }
        }
    }

    fun selectByDateRange(start: Long?, end: Long?) {
        viewModelScope.launch {
            val min = (start ?: 0L).toLocalDate()
            val max = (end ?: Long.MAX_VALUE).toLocalDate()
            activities.update { current ->
                val currentIds = current.map { it.id }
                current + allActivities.first().filter { activity ->
                    activity.date != null && activity.date >= min && activity.date <= max && activity.id !in currentIds
                }
            }
        }
    }

    fun select(activities: List<Activity>) {
        this.activities.value = activities
    }

    fun addArchive() {
        viewModelScope.launch {
            val activities = activities.value
            _progressState.value = CreateArchiveProgressState(createdArchive = ArchiveState.InProgress)
            dataSource.addArchive(
                name = name.value,
                activities = activities,
                participants = activities.affectedParticipants,
                criteria = activities.affectedCriteria
            ).onSuccess {
                _progressState.update {
                    it?.copy(createdArchive = ArchiveState.Success, deletedActivities = ArchiveState.InProgress)
                }
                activityDataSource.deleteActivities(activities.map { it.id }).onSuccess {
                    _progressState.update { it?.copy(deletedActivities = ArchiveState.Success) }
                }.onFailure {
                    _progressState.update { it?.copy(deletedActivities = ArchiveState.Fail) }
                }
                if (deleteParticipants.value) {
                    _progressState.update {
                        it?.copy(deletedParticipants = ArchiveState.InProgress)
                    }
                    participantDataSource.deleteParticipants(activities.affectedParticipants.map { it.id }).onSuccess {
                        _progressState.update { it?.copy(deletedParticipants = ArchiveState.Success) }
                    }.onFailure {
                        _progressState.update { it?.copy(deletedParticipants = ArchiveState.Fail) }
                    }
                }
                if (deleteCriteria.value) {
                    _progressState.update {
                        it?.copy(deletedCriteria = ArchiveState.InProgress)
                    }
                    criteriaDataSource.deleteCriteria(activities.affectedCriteria.map { it.id }).onSuccess {
                        _progressState.update { it?.copy(deletedCriteria = ArchiveState.Success) }
                    }.onFailure {
                        _progressState.update { it?.copy(deletedCriteria = ArchiveState.Fail) }
                    }
                }
                _progressState.update { it?.copy(finished = true) }
            }.onFailure {
                _progressState.value = null
                _result.send(Result.failure(it))
            }
        }
    }

    fun activities(): List<Activity> = activities.value

    private val List<Activity>.affectedParticipants: List<Participant>
        get() = flatMap { it.participants }.distinctBy { it.id }

    private val List<Activity>.affectedCriteria: List<ActivityCriteria>
        get() = flatMap { it.criteria }.distinctBy { it.id }
}