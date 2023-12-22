package com.andradel.pathfinders.user

import com.andradel.pathfinders.model.ParticipantClass

sealed interface UserRole {
    data object Admin : UserRole
    data class ClassAdmin(val participantClass: ParticipantClass) : UserRole
    data object User : UserRole
}

val UserRole.isAdmin
    get() = this is UserRole.Admin || this is UserRole.ClassAdmin