package com.andradel.pathfinders.user

import android.os.Parcelable
import com.andradel.pathfinders.model.ParticipantClass
import kotlinx.parcelize.Parcelize

sealed interface UserRole : Parcelable {
    @Parcelize
    data object Admin : UserRole

    @Parcelize
    data class ClassAdmin(val classes: Set<ParticipantClass>) : UserRole

    @Parcelize
    data object User : UserRole
}

val UserRole.isClassAdmin
    get() = this is UserRole.Admin || this is UserRole.ClassAdmin