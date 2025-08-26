package com.andradel.pathfinders.shared.firebase.activity

import com.andradel.pathfinders.shared.model.criteria.ActivityCriteria
import org.koin.core.annotation.Factory

@Factory
class ActivityCriteriaMapper {
    fun toCriteria(key: String, value: FirebaseActivityCriteria): ActivityCriteria {
        return ActivityCriteria(key, value.name, value.maxScore)
    }

    fun toFirebaseCriteria(value: ActivityCriteria): FirebaseActivityCriteria {
        return FirebaseActivityCriteria(value.name, value.maxScore)
    }
}