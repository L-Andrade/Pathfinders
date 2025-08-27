package com.andradel.pathfinders.shared.firebase.participant

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseParticipant(
    val name: String = "",
    val email: String? = null,
    val contact: String? = null,
    val scoutClass: String? = null,
    val dateOfBirth: String? = null,
)