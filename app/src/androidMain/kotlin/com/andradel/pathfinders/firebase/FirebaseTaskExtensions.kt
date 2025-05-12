package com.andradel.pathfinders.firebase

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

suspend fun <T> Task<T>.awaitWithTimeout(): T = withTimeout(10_000L) { await() }