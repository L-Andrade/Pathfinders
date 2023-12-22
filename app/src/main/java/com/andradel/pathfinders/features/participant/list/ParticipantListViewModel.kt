package com.andradel.pathfinders.features.participant.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.activity.participantPoints
import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.user.UserSession
import com.andradel.pathfinders.user.isAdmin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipantListViewModel @Inject constructor(
    private val dataSource: ParticipantFirebaseDataSource,
    activityDataSource: ActivityFirebaseDataSource,
    userSession: UserSession,
) : ViewModel() {
    private val collapsed = MutableStateFlow<Map<ParticipantClass, Boolean>>(emptyMap())
    private val sorting = MutableStateFlow(ParticipantSort.NameAsc)

    val state: StateFlow<ParticipantListState> =
        combine(
            dataSource.participants,
            activityDataSource.activities,
            collapsed,
            sorting,
        ) { participants, activities, collapsed, sorting ->
            val participantsWithTotalScore = participants.map { p ->
                ParticipantWithTotalScore(p, activities.sumOf { it.participantPoints(participantId = p.id) })
            }.sort(sorting)
            val groupedParticipants = participantsWithTotalScore
                .groupBy { p -> p.participant.participantClass }
                .toSortedMap()
                .map { (scoutClass, participants) ->
                    ParticipantSection(scoutClass, participants, collapsed[scoutClass] ?: false)
                }
            ParticipantListState.Loaded(groupedParticipants, sorting)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParticipantListState.Loading)

    val isAdmin: StateFlow<Boolean> = userSession.isAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun deleteParticipant(participant: Participant) {
        viewModelScope.launch {
            dataSource.deleteParticipant(participant.id)
        }
    }

    fun collapseSection(participantClass: ParticipantClass) {
        collapsed.value = collapsed.value.toMutableMap().apply { this[participantClass] = !(this[participantClass] ?: false) }
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