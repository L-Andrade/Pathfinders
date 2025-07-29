package com.andradel.pathfinders.firebase.functions.model

import com.andradel.pathfinders.model.ParticipantClass
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseRoleRequest(
    val email: String,
    val role: Role,
    val classes: Set<ParticipantClass>?,
)