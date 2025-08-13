package com.andradel.pathfinders.shared.features.admin.role

import com.andradel.pathfinders.flavors.model.ParticipantClass
import com.andradel.pathfinders.shared.features.admin.role.model.EditUserRole

data class EditUserRoleState(
    val name: String,
    val role: EditUserRole,
    val classes: Set<ParticipantClass>?,
    val enabled: Boolean = false,
    val loading: Boolean = false,
)