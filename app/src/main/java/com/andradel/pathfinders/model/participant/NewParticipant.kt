package com.andradel.pathfinders.model.participant

import com.andradel.pathfinders.model.ScoutClass

data class NewParticipant(val name: String, val email: String?, val scoutClass: ScoutClass)