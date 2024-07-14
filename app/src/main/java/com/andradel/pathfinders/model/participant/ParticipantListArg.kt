package com.andradel.pathfinders.model.participant

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParticipantListArg(val archiveName: String? = null) : Parcelable