package com.andradel.pathfinders.model.user

import android.os.Parcelable
import com.andradel.pathfinders.user.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserArg(val user: User) : Parcelable