package com.andradel.pathfinders.shared.extensions

import kotlinx.coroutines.TimeoutCancellationException
import kotlin.coroutines.cancellation.CancellationException

fun <T> Result<T>.throwCancellation(): Result<T> = onFailure {
    if (it !is TimeoutCancellationException && it is CancellationException) throw it
}