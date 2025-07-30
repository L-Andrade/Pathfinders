package com.andradel.pathfinders.shared.features.participant.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andradel.pathfinders.shared.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.shared.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.shared.model.ParticipantClass
import com.andradel.pathfinders.shared.model.activity.participantPoints
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.nav.NavigationRoute
import com.andradel.pathfinders.shared.user.UserSession
import com.andradel.pathfinders.shared.user.isAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ParticipantListViewModel(
    handle: SavedStateHandle,
    private val dataSource: ParticipantFirebaseDataSource,
    activityDataSource: ActivityFirebaseDataSource,
    userSession: UserSession,
) : ViewModel() {
    private val collapsed = MutableStateFlow<Map<ParticipantClass, Boolean>>(emptyMap())
    private val sorting = MutableStateFlow(ParticipantSort.NameAsc)
    val archiveName = handle.toRoute<NavigationRoute.ParticipantList>().archiveName
    private val isArchived = archiveName != null

    val state: StateFlow<ParticipantListState> =
        combine(
            dataSource.participants(archiveName),
            activityDataSource.activities(archiveName),
            collapsed,
            sorting,
        ) { participants, activities, collapsed, sorting ->
            val participantsWithTotalScore = participants.map { p ->
                ParticipantWithTotalScore(p, activities.sumOf { it.participantPoints(participantId = p.id) })
            }.sort(sorting)
            val groupedParticipants = participantsWithTotalScore
                .groupBy { p -> p.participant.participantClass }
                .map { (scoutClass, participants) ->
                    ParticipantSection(scoutClass, participants, collapsed[scoutClass] ?: false)
                }
                .sortedBy { it.participantClass }
            ParticipantListState.Loaded(groupedParticipants, sorting)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParticipantListState.Loading)

    val canModify: StateFlow<Boolean> = userSession.isAdmin.map { isAdmin -> isAdmin && !isArchived }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun deleteParticipant(participant: Participant) {
        viewModelScope.launch {
            dataSource.deleteParticipant(participant.id)
        }
    }

    fun collapseSection(participantClass: ParticipantClass) {
        collapsed.update { current -> current + (participantClass to !(current[participantClass] ?: false)) }
    }

    fun sortBy(sorting: ParticipantSort) {
        this.sorting.value = sorting
    }

    private fun List<ParticipantWithTotalScore>.sort(sorting: ParticipantSort): List<ParticipantWithTotalScore> {
        return when (sorting) {
            ParticipantSort.PointsAsc -> sortedBy { it.score }
            ParticipantSort.PointsDesc -> sortedBy { -it.score }
            ParticipantSort.NameAsc -> sortedBy { it.participant.name }
            ParticipantSort.NameDesc -> sortedByDescending { it.participant.name }
        }
    }
}