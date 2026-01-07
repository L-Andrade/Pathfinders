package com.andradel.pathfinders.shared.features.activity.evaluate.team

import com.andradel.pathfinders.shared.model.activity.TeamScores
import com.andradel.pathfinders.shared.model.team.Team

sealed interface EvaluateTeamActivityState {
    data object Loading : EvaluateTeamActivityState
    data object Empty : EvaluateTeamActivityState
    data class Loaded(val teams: List<Team>, val scores: TeamScores, val loading: Boolean) : EvaluateTeamActivityState
}