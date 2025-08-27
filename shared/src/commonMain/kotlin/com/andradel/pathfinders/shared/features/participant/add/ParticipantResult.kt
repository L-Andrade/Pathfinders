package com.andradel.pathfinders.shared.features.participant.add

import org.jetbrains.compose.resources.StringResource

sealed interface ParticipantResult {
    object Success : ParticipantResult
    object Loading : ParticipantResult
    data class Failure(val message: StringResource) : ParticipantResult
}