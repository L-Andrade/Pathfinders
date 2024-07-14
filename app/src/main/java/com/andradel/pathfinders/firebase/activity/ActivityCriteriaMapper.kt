package com.andradel.pathfinders.firebase.activity

import com.andradel.pathfinders.model.criteria.ActivityCriteria
import javax.inject.Inject

class ActivityCriteriaMapper @Inject constructor() {
    fun toCriteria(key: String, value: FirebaseActivityCriteria): ActivityCriteria {
        return ActivityCriteria(key, value.name, value.maxScore)
    }

    fun toFirebaseCriteria(value: ActivityCriteria): FirebaseActivityCriteria {
        return FirebaseActivityCriteria(value.name, value.maxScore)
    }
}