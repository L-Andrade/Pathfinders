package com.andradel.pathfinders.firebase

import com.andradel.pathfinders.model.ParticipantClass

fun String?.toClass(): ParticipantClass =
    ParticipantClass.options.find { it.name.equals(this, ignoreCase = true) } ?: ParticipantClass.Unknown