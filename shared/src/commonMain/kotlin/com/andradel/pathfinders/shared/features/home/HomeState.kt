package com.andradel.pathfinders.shared.features.home

import com.andradel.pathfinders.shared.model.participant.Participant
import com.andradel.pathfinders.shared.user.User

sealed interface HomeState {
    object Loading : HomeState
    object Guest : HomeState
    object Error : HomeState
    data class Loaded(val participant: Participant?, val user: User) : HomeState
}