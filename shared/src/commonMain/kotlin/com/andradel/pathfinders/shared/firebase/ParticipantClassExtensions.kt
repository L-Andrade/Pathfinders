package com.andradel.pathfinders.shared.firebase

import com.andradel.pathfinders.flavors.model.ParticipantClass

fun String?.toClass(): ParticipantClass =
    ParticipantClass.options.find { it.name.equals(this, ignoreCase = true) } ?: ParticipantClass.Unknown