package com.andradel.pathfinders.firebase.archive

import androidx.annotation.Keep
import com.andradel.pathfinders.firebase.activity.FirebaseActivity
import com.andradel.pathfinders.firebase.activity.FirebaseActivityCriteria
import com.andradel.pathfinders.firebase.participant.FirebaseParticipant

@Keep
data class FirebaseArchive(
    val activities: Map<String, FirebaseActivity> = emptyMap(),
    val criteria: Map<String, FirebaseActivityCriteria> = emptyMap(),
    val participants: Map<String, FirebaseParticipant> = emptyMap(),
)