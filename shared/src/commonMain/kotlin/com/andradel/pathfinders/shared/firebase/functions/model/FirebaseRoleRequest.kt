package com.andradel.pathfinders.shared.firebase.functions.model

import com.andradel.pathfinders.flavors.model.ParticipantClass
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseRoleRequest(
    val email: String,
    val role: Role,
    val classes: Set<ParticipantClass>?,
)