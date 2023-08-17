package com.andradel.pathfinders.firebase.activity

import com.andradel.pathfinders.firebase.toMapFlow
import com.andradel.pathfinders.model.activity.ActivityCriteria
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ActivityCriteriaFirebaseDataSource @Inject constructor(
    db: FirebaseDatabase,
) {
    private val criteriaRef = db.reference.child("criteria")

    val criteria: Flow<List<ActivityCriteria>> = criteriaRef.toMapFlow<FirebaseActivityCriteria>().map { criteriaMap ->
        criteriaMap.map { (key, value) -> ActivityCriteria(key, value.name, value.maxScore) }
    }

    suspend fun addCriteria(criteria: String, maxScore: Int) {
        val key = requireNotNull(criteriaRef.push().key)
        criteriaRef.child(key).setValue(FirebaseActivityCriteria(criteria, maxScore)).await()
    }
}