package com.andradel.pathfinders.shared.firebase.team

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseTeam(
    val name: String = "",
    val participantIds: List<String> = emptyList(),
)