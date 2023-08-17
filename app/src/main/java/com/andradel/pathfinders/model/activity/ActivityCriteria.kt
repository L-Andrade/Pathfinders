package com.andradel.pathfinders.model.activity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivityCriteria(
    val id: String,
    val name: String,
    val maxScore: Int,
) : Parcelable