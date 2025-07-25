package com.andradel.pathfinders.features.participant.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.model.participant.Participant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class ParticipantProfileViewModel(
    handle: SavedStateHandle,
    dataSource: ParticipantFirebaseDataSource,
    activityDataSource: ActivityFirebaseDataSource,
) : ViewModel() {
    private val participant = handle.get<Participant>("participant")!!
    private val archiveName = handle.get<String?>("archiveName")

    val state: StateFlow<ParticipantProfileState> =
        combine(
            dataSource.participant(archiveName, participant.id),
            activityDataSource.activitiesForUser(archiveName, participant.id),
        ) { participant, activities ->
            ParticipantProfileState.Loaded(participant ?: this.participant, activities)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParticipantProfileState.Loading(participant))
}