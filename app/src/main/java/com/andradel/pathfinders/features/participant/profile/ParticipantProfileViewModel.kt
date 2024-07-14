package com.andradel.pathfinders.features.participant.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.features.navArgs
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.model.participant.ParticipantArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ParticipantProfileViewModel @Inject constructor(
    handle: SavedStateHandle,
    dataSource: ParticipantFirebaseDataSource,
    activityDataSource: ActivityFirebaseDataSource
) : ViewModel() {
    private val args = handle.navArgs<ParticipantArg>()
    private val participant = args.participant

    val state: StateFlow<ParticipantProfileState> =
        combine(
            dataSource.participant(args.archiveName, participant.id),
            activityDataSource.activitiesForUser(args.archiveName, participant.id)
        ) { participant, activities ->
            ParticipantProfileState.Loaded(participant ?: this.participant, activities)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParticipantProfileState.Loading(participant))
}