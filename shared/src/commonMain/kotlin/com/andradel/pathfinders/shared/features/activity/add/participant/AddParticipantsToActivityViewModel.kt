package com.andradel.pathfinders.shared.features.activity.add.participant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.shared.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.model.participant.SelectedParticipants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class AddParticipantsToActivityViewModel(
    @InjectedParam selected: SelectedParticipants,
    dataSource: ParticipantFirebaseDataSource,
) : ViewModel() {
    private val initialSelection = selected.participants
    private val selection = MutableStateFlow(initialSelection)
    private val selectedClasses = selected.classes
    private val filteringByClass = MutableStateFlow(selectedClasses.isNotEmpty())

    val state = combine(
        selection,
        dataSource.participants(null),
        filteringByClass,
    ) { selection, participants, filteringByClass ->
        AddParticipantsToActivityState.Loaded(
            selection = selection,
            participants = participants
                .filter { it !in selection }
                .let { list -> if (filteringByClass) list.filter { it.participantClass in selectedClasses } else list },
            filteringByClass = filteringByClass,
            classes = selectedClasses,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddParticipantsToActivityState.Loading)

    val isUnsaved
        get() = initialSelection != selection.value

    fun setFilteringByClass(checked: Boolean) {
        filteringByClass.value = checked
    }

    fun selectParticipant(participant: Participant) {
        selection.update { selection -> selection + participant }
    }

    fun unselectParticipant(participant: Participant) {
        selection.update { selection -> selection - participant }
    }
}