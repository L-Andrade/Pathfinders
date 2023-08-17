package com.andradel.pathfinders.model.participant

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParticipantArg(val participant: Participant) : Parcelable