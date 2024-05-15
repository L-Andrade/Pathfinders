package com.andradel.pathfinders.model.criteria

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CriteriaSelectionArg(
    val selection: ArrayList<ActivityCriteria>
) : Parcelable