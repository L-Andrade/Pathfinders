package com.andradel.pathfinders.model.activity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivitySelectionArg(val selection: ArrayList<Activity>) : Parcelable