package com.andradel.pathfinders.shared.features.participant.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andradel.pathfinders.shared.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.shared.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.nav.customNavType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import kotlin.reflect.typeOf


@KoinViewModel
class ParticipantProfileViewModel(
    handle: SavedStateHandle,
    dataSource: ParticipantFirebaseDataSource,
    activityDataSource: ActivityFirebaseDataSource,
) : ViewModel() {
    private val route = handle.toRoute<NavigationRoute.ParticipantProfile>(
        typeMap = mapOf(typeOf<Participant>() to customNavType<Participant>()),
    )

    private val participant = route.participant
    private val archiveName = route.archiveName

    val state: StateFlow<ParticipantProfileState> =
        combine(
            dataSource.participant(archiveName, participant.id),
            activityDataSource.activitiesForUser(archiveName, participant.id),
        ) { participant, activities ->
            ParticipantProfileState.Loaded(participant ?: this.participant, activities)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParticipantProfileState.Loading(participant))
}