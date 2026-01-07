package com.andradel.pathfinders.shared.features.team.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.shared.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.shared.firebase.team.TeamFirebaseDataSource
import com.andradel.pathfinders.shared.model.activity.teamPoints
import com.andradel.pathfinders.shared.model.activity.teamPointsForParticipant
import com.andradel.pathfinders.shared.model.team.Team
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class TeamProfileViewModel(
    @InjectedParam private val team: Team,
    @InjectedParam private val archiveName: String?,
    dataSource: TeamFirebaseDataSource,
    activityDataSource: ActivityFirebaseDataSource,
) : ViewModel() {

    val state: StateFlow<TeamProfileState> =
        combine(
            dataSource.team(team.id, archiveName),
            activityDataSource.activitiesForTeam(archiveName, team.id),
        ) { team, activities ->
            val team = team ?: this.team
            TeamProfileState.Loaded(
                title = team.name,
                teamParticipants = team.participants.map { participant ->
                    val participantActivities = activities
                        .filter { it.teamScores[team.id]?.containsKey(participant.id) == true }
                        .map {
                            TeamParticipantActivity(
                                id = it.id,
                                name = it.name,
                                points = it.teamPointsForParticipant(team.id, participant.id),
                            )
                        }
                    TeamParticipant(
                        id = participant.id,
                        name = participant.name,
                        points = participantActivities.sumOf { it.points },
                        activities = participantActivities,
                    )
                }.sortedWith(compareBy({ -it.activities.size }, { -it.points })),
                teamActivities = activities.sortedByDescending { it.date }.map { activity ->
                    val participantIds = activity.teamScores[team.id]?.keys.orEmpty()
                    TeamActivity(
                        id = activity.id,
                        name = activity.name,
                        date = activity.date?.toString(),
                        participants = participantIds.map { id ->
                            val participant = team.participants.find { it.id == id }
                            TeamActivityParticipant(
                                id = id,
                                name = participant?.name,
                                points = activity.teamPointsForParticipant(team.id, id),
                            )
                        }.sortedByDescending { it.name },
                        total = activity.teamPoints(team.id),
                    )
                },
                points = activities.sumOf { it.teamPoints(this.team.id) },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamProfileState.Loading(team.name))
}