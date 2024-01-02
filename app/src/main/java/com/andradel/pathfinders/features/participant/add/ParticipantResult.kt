package com.andradel.pathfinders.features.participant.add

import androidx.annotation.StringRes

sealed interface ParticipantResult {
    object Success : ParticipantResult
    object Loading : ParticipantResult
    data class Failure(@StringRes val message: Int) : ParticipantResult
}