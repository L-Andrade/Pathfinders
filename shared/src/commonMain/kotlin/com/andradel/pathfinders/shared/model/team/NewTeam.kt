package com.andradel.pathfinders.shared.model.team

import com.andradel.pathfinders.shared.model.participant.Participant

data class NewTeam(val name: String, val participants: List<Participant>)