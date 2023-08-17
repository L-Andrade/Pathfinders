package com.andradel.pathfinders.features.activity.add

sealed interface AddActivityResult {
    object Failure : AddActivityResult
    object Success : AddActivityResult
}