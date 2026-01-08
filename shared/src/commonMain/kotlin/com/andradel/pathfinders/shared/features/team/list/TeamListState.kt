package com.andradel.pathfinders.shared.features.team.list

import com.andradel.pathfinders.shared.model.team.Team

sealed interface TeamListState {
    val canAdd: Boolean
    val canDelete: Boolean

    data class Loaded(
        val teams: List<TeamItem>,
        override val canAdd: Boolean,
        override val canDelete: Boolean,
    ) : TeamListState

    data object Loading : TeamListState {
        override val canAdd: Boolean = false
        override val canDelete: Boolean = false
    }

    data class Empty(override val canAdd: Boolean) : TeamListState {
        override val canDelete: Boolean = false
    }
}

data class TeamItem(val team: Team, val points: Int)