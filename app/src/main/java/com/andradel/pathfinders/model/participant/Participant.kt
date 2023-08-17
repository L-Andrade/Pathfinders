package com.andradel.pathfinders.model.participant

import android.os.Parcelable
import com.andradel.pathfinders.model.ScoutClass
import kotlinx.parcelize.Parcelize

@Parcelize
data class Participant(
    val id: String,
    val name: String,
    val email: String?,
    val scoutClass: ScoutClass
) : Parcelable