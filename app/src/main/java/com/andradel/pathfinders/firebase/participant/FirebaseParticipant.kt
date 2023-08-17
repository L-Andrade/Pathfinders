package com.andradel.pathfinders.firebase.participant

import androidx.annotation.Keep
import com.andradel.pathfinders.model.ScoutClass

@Keep
data class FirebaseParticipant(
    val name: String = "",
    val email: String? = null,
    val scoutClass: ScoutClass = ScoutClass.Invalid
)