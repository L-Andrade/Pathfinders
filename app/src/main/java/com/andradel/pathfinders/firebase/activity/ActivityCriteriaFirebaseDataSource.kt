package com.andradel.pathfinders.firebase.activity

import com.andradel.pathfinders.extensions.throwCancellation
import com.andradel.pathfinders.firebase.awaitWithTimeout
import com.andradel.pathfinders.firebase.toMapFlow
import com.andradel.pathfinders.model.criteria.ActivityCriteria
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class ActivityCriteriaFirebaseDataSource(
    db: FirebaseDatabase,
    private val mapper: ActivityCriteriaMapper,
) {
    private val criteriaRef = db.reference.child("criteria")

    val criteria: Flow<List<ActivityCriteria>> = criteriaRef.toMapFlow<FirebaseActivityCriteria>().map { criteriaMap ->
        criteriaMap.map { (key, value) -> mapper.toCriteria(key, value) }
    }

    suspend fun addCriteria(criteria: String, maxScore: Int): Result<Unit> = runCatching {
        val key = requireNotNull(criteriaRef.push().key)
        criteriaRef.child(key).setValue(FirebaseActivityCriteria(criteria, maxScore)).awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun deleteCriteria(criteriaId: String) = runCatching {
        criteriaRef.child(criteriaId).removeValue().awaitWithTimeout()
        Unit
    }.throwCancellation()

    suspend fun deleteCriteria(criteriaIds: List<String>): Result<Unit> = runCatching {
        criteriaRef.updateChildren(criteriaIds.associateWith { null }).awaitWithTimeout()
        Unit
    }.throwCancellation()
}