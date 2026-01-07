package com.andradel.pathfinders.shared.features.activity.evaluate.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.shared.firebase.activity.ActivityFirebaseDataSource
import com.andradel.pathfinders.shared.firebase.team.TeamFirebaseDataSource
import com.andradel.pathfinders.shared.model.activity.Activity
import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.model.team.Team
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class EvaluateTeamActivityViewModel(
    @InjectedParam private val activity: Activity,
    private val dataSource: ActivityFirebaseDataSource,
    teamDataSource: TeamFirebaseDataSource,
) : ViewModel() {

    private val scores = MutableStateFlow(activity.teamScores)
    private val loading = MutableStateFlow(false)

    private val teams = teamDataSource.teams(activity.archiveName).map { teams ->
        val participantIds = activity.participants.map { it.id }
        teams.filter { team -> team.participants.any { it.id in participantIds } }
    }

    val state = combine(scores, loading, teams) { scores, loading, teams ->
        if (teams.isEmpty()) {
            EvaluateTeamActivityState.Empty
        } else {
            EvaluateTeamActivityState.Loaded(teams = teams, scores = scores, loading = loading)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EvaluateTeamActivityState.Loading)

    private val _result = Channel<Result<Unit>>()
    val result = _result.receiveAsFlow()

    val isUnsaved: Boolean
        get() = scores.value != activity.teamScores

    fun setTeamScore(team: Team, criteriaId: String, score: Int) {
        scores.update { scores ->
            val teamScores = scores[team.id].orEmpty()
            val participantScores = team.participants.associate { participant ->
                participant.id to teamScores[participant.id].orEmpty() + (criteriaId to score)
            }
            scores + (team.id to participantScores)
        }
    }

    fun setParticipantScore(team: Team, participant: Participant, criteriaId: String, score: Int) {
        scores.update { scores ->
            val teamScores = scores[team.id].orEmpty()
            val participantScores = teamScores[participant.id].orEmpty() + (criteriaId to score)
            scores + (team.id to (teamScores + (participant.id to participantScores)))
        }
    }

    fun onSave() {
        loading.value = true
        viewModelScope.launch {
            _result.send(dataSource.updateTeamScores(activityId = activity.id, scores = scores.value))
            loading.value = false
        }
    }
}