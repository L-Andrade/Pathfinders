package com.andradel.pathfinders.shared.features.activity.add.participant

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andradel.pathfinders.shared.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.customNavType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel
import kotlin.reflect.typeOf


@KoinViewModel
class AddParticipantsToActivityViewModel(
    handle: SavedStateHandle,
    dataSource: ParticipantFirebaseDataSource,
) : ViewModel() {
    private val route = handle.toRoute<NavigationRoute.AddParticipantsToActivity>(
        typeMap = mapOf(typeOf<List<Participant>>() to customNavType<List<Participant>>())
    )
    private val initialSelection = route.participants
    private val selection = MutableStateFlow(initialSelection)
    private val selectedClasses = route.classes
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