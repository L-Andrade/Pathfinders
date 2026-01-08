package com.andradel.pathfinders.shared.features.team.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.shared.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.shared.firebase.team.TeamFirebaseDataSource
import com.andradel.pathfinders.shared.model.activity.teamPoints
import com.andradel.pathfinders.shared.user.UserRole
import com.andradel.pathfinders.shared.user.UserSession
import com.andradel.pathfinders.shared.user.role
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TeamListViewModel(
    private val archiveName: String?,
    private val dataSource: TeamFirebaseDataSource,
    activityDataSource: ActivityFirebaseDataSource,
    userSession: UserSession,
) : ViewModel() {

    val state = combine(
        dataSource.teams(archiveName),
        activityDataSource.activities(archiveName),
        userSession.role,
    ) { teams, activities, role ->
        val canAdd = role is UserRole.Admin && archiveName == null
        if (teams.isEmpty()) {
            TeamListState.Empty(canAdd)
        } else {
            TeamListState.Loaded(
                teams = teams.map { team -> TeamItem(team, activities.sumOf { it.teamPoints(team.id) }) }
                    .sortedByDescending { it.points },
                canAdd = canAdd,
                canDelete = canAdd,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamListState.Loading)

    fun delete(teamId: String) {
        viewModelScope.launch {
            dataSource.delete(teamId)
        }
    }
}