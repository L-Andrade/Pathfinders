package com.andradel.pathfinders.model.activity

import android.os.Parcelable
import com.andradel.pathfinders.model.ParticipantClass
import com.andradel.pathfinders.model.participant.Participant
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParticipantSelectionArg(
    val _selection: ArrayList<Participant> = arrayListOf(),
    val _classes: ArrayList<String> = arrayListOf(),
) : Parcelable {
    constructor(selection: List<Participant>, classes: List<ParticipantClass>) : this(
        ArrayList(selection),
        ArrayList(classes.map { it.name })
    )

    @IgnoredOnParcel
    val selection: List<Participant> = _selection.toList()

    @IgnoredOnParcel
    val classes: List<ParticipantClass> = _classes.map { ParticipantClass.valueOf(it) }
}