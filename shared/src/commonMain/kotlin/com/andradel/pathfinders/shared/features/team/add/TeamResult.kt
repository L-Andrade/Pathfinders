package com.andradel.pathfinders.shared.features.team.add

sealed interface TeamResult {
    data object Success : TeamResult
    data object Loading : TeamResult
    data object Failure : TeamResult
}