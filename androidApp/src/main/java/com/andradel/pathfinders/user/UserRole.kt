package com.andradel.pathfinders.user

import com.andradel.pathfinders.model.ParticipantClass
import kotlinx.serialization.Serializable

@Serializable
sealed interface UserRole {
    @Serializable
    data object Admin : UserRole

    @Serializable
    data class ClassAdmin(val classes: Set<ParticipantClass>) : UserRole

    @Serializable
    data object User : UserRole
}

val UserRole.isClassAdmin
    get() = this is UserRole.Admin || this is UserRole.ClassAdmin