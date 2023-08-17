package com.andradel.pathfinders.features.activity.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.extensions.combine
import com.andradel.pathfinders.features.navArgs
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.model.ScoutClass
import com.andradel.pathfinders.model.activity.Activity
import com.andradel.pathfinders.model.activity.ActivityCriteria
import com.andradel.pathfinders.model.activity.NewActivity
import com.andradel.pathfinders.model.activity.OptionalActivityArg
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.validation.NameValidation
import com.andradel.pathfinders.validation.isValid
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AddEditActivityViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dataSource: ActivityFirebaseDataSource,
    private val nameValidation: NameValidation,
) : ViewModel() {
    private val activity = handle.navArgs<OptionalActivityArg>().activity

    private val participants = MutableStateFlow(activity?.participants.orEmpty())
    private val criteria = MutableStateFlow(activity?.criteria.orEmpty())
    private val classes = MutableStateFlow(activity?.classes.orEmpty())
    private val name = MutableStateFlow(activity?.name.orEmpty())
    private val date = MutableStateFlow(activity?.date?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) })
    private val addActivityResult = MutableStateFlow<AddActivityResult?>(null)

    val state = combine(
        name,
        date,
        participants,
        classes,
        criteria,
        addActivityResult
    ) { name, date, participants, classes, criteria, addActivityResult ->
        val nameValidation = nameValidation.validate(name)
        AddEditActivityState(
            name = name,
            nameValidation = nameValidation,
            date = date,
            classes = classes,
            participants = participants,
            criteria = criteria,
            isValid = nameValidation.isValid,
            addActivityResult = addActivityResult
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddEditActivityState())

    val isEditing = activity != null
    val isUnsaved: Boolean
        get() = activity == null || activity.toNewActivity() != state.value.toNewActivity()

    fun addActivity() {
        viewModelScope.launch {
            dataSource.addOrUpdateActivity(state.value.toNewActivity(), activityId = activity?.id)
            addActivityResult.value = AddActivityResult.Success
        }
    }

    private fun Activity.toNewActivity(): NewActivity = NewActivity(
        name = name,
        date = date,
        participants = participants,
        classes = classes,
        criteria = criteria,
        scores = scores
    )

    private fun AddEditActivityState.toNewActivity(): NewActivity = NewActivity(
        name = name,
        date = date?.toString().orEmpty(),
        participants = participants,
        classes = classes,
        criteria = criteria,
        scores = activity?.scores.orEmpty()
    )

    fun setSelection(participants: List<Participant>) {
        this.participants.value = participants
    }

    fun setCriteriaSelection(criteria: List<ActivityCriteria>) {
        this.criteria.value = criteria
    }

    fun setClassSelected(scoutClass: ScoutClass, selected: Boolean) {
        classes.value = if (selected) classes.value + scoutClass else classes.value - scoutClass
    }

    fun setAllSelected(selected: Boolean) {
        classes.value = if (selected) ScoutClass.options else emptyList()
    }

    fun updateName(name: String) {
        this.name.value = name
    }

    fun updateDate(date: LocalDate) {
        this.date.value = date
    }

    fun deleteActivity() {
        if (activity != null) {
            viewModelScope.launch {
                dataSource.deleteActivity(activity.id)
            }
        }
    }
}
