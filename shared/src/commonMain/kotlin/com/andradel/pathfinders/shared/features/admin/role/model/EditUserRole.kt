package com.andradel.pathfinders.shared.features.admin.role.model

import org.jetbrains.compose.resources.StringResource
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.admin_role
import pathfinders.shared.generated.resources.class_admin_role
import pathfinders.shared.generated.resources.user_role

enum class EditUserRole {
    Admin,
    ClassAdmin,
    User,
}

val EditUserRole.stringRes: StringResource
    get() = when (this) {
        EditUserRole.Admin -> Res.string.admin_role
        EditUserRole.ClassAdmin -> Res.string.class_admin_role
        EditUserRole.User -> Res.string.user_role
    }