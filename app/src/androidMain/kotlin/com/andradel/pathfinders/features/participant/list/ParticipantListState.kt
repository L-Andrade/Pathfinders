package com.andradel.pathfinders.features.participant.list

sealed interface ParticipantListState {
    object Loading : ParticipantListState
    data class Loaded(val participants: List<ParticipantSection>, val sort: ParticipantSort) : ParticipantListState
}