package com.andradel.pathfinders.shared.firebase.activity

import com.andradel.pathfinders.shared.extensions.throwCancellation
import com.andradel.pathfinders.shared.firebase.toMapFlow
import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class ActivityCriteriaFirebaseDataSource(
    private val mapper: ActivityCriteriaMapper,
) {
    private val db = Firebase.database
    private val criteriaRef = db.reference().child("criteria")

    val criteria: Flow<List<ActivityCriteria>> = criteriaRef.toMapFlow<FirebaseActivityCriteria>().map { criteriaMap ->
        criteriaMap.map { (key, value) -> mapper.toCriteria(key, value) }
    }

    suspend fun addCriteria(criteria: String, maxScore: Int): Result<Unit> = runCatching {
        val key = requireNotNull(criteriaRef.push().key)
        criteriaRef.child(key).setValue(FirebaseActivityCriteria(criteria, maxScore))
        Unit
    }.throwCancellation()

    suspend fun deleteCriteria(criteriaId: String) = runCatching {
        criteriaRef.child(criteriaId).removeValue()
    }.throwCancellation()

    suspend fun deleteCriteria(criteriaIds: List<String>): Result<Unit> = runCatching {
        criteriaRef.updateChildren(criteriaIds.associateWith { null })
    }.throwCancellation()
}