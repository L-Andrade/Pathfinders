package com.andradel.pathfinders.features.home

import com.andradel.pathfinders.model.participant.Participant
import com.andradel.pathfinders.user.User

sealed interface HomeState {
    object Loading : HomeState
    object Guest : HomeState
    data class Loaded(val participant: Participant?, val user: User) : HomeState
}