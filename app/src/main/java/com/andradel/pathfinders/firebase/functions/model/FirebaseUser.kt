package com.andradel.pathfinders.firebase.functions.model

import com.andradel.pathfinders.model.ParticipantClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUser(
    val name: String?,
    val email: String?,
    val role: Role = Role.User,
    val classes: Set<ParticipantClass> = emptySet(),
)

@Serializable
enum class Role {
    @SerialName("admin")
    Admin,

    @SerialName("class")
    ClassAdmin,

    @SerialName("user")
    User
}