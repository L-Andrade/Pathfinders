package com.andradel.pathfinders.extensions

import kotlinx.coroutines.TimeoutCancellationException
import kotlin.coroutines.cancellation.CancellationException

fun <T> Result<T>.throwCancellation(): Result<T> = onFailure {
    if (it is CancellationException && it !is TimeoutCancellationException) throw it
}