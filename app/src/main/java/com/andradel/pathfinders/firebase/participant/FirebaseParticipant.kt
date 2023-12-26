package com.andradel.pathfinders.firebase.participant

import androidx.annotation.Keep
import com.andradel.pathfinders.model.ParticipantClass

@Keep
data class FirebaseParticipant(
    val name: String = "",
    val email: String? = null,
    val scoutClass: ParticipantClass = ParticipantClass.Invalid,
    val dateOfBirth: String? = null,
)