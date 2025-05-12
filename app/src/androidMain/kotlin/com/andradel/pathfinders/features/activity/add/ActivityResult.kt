package com.andradel.pathfinders.features.activity.add

sealed interface ActivityResult {
    object Failure : ActivityResult
    object Success : ActivityResult
    object Loading : ActivityResult
}