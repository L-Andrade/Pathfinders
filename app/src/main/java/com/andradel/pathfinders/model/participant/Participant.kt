package com.andradel.pathfinders.model.participant

import android.os.Parcelable
import com.andradel.pathfinders.model.ParticipantClass
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Participant(
    val id: String,
    val name: String,
    val email: String?,
    val participantClass: ParticipantClass,
    val dateOfBirth: LocalDate?,
) : Parcelable