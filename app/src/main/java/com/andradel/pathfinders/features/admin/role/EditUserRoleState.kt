package com.andradel.pathfinders.features.admin.role

import com.andradel.pathfinders.features.admin.role.model.EditUserRole
import com.andradel.pathfinders.model.ParticipantClass

data class EditUserRoleState(
    val name: String,
    val role: EditUserRole,
    val classes: Set<ParticipantClass>?,
    val enabled: Boolean = false,
    val loading: Boolean = false,
)