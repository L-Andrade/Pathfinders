package com.andradel.pathfinders.features.admin.role.model

import androidx.annotation.StringRes
import com.andradel.pathfinders.R

enum class EditUserRole {
    Admin,
    ClassAdmin,
    User
}

val EditUserRole.stringRes: Int
    @StringRes get() = when (this) {
        EditUserRole.Admin -> R.string.admin_role
        EditUserRole.ClassAdmin -> R.string.class_admin_role
        EditUserRole.User -> R.string.user_role
    }