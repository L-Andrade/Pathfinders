package com.andradel.pathfinders.shared.firebase.archive

import com.andradel.pathfinders.shared.firebase.activity.FirebaseActivity
import com.andradel.pathfinders.shared.firebase.activity.FirebaseActivityCriteria
import com.andradel.pathfinders.shared.firebase.participant.FirebaseParticipant
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseArchive(
    val activities: Map<String, FirebaseActivity> = emptyMap(),
    val criteria: Map<String, FirebaseActivityCriteria> = emptyMap(),
    val participants: Map<String, FirebaseParticipant> = emptyMap(),
)