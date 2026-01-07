package com.andradel.pathfinders.shared.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberResultStore(): ResultStore {
    return remember { ResultStore() }
}

class ResultStore {

    val resultStateMap: MutableMap<String, MutableState<Any?>> = mutableStateMapOf()

    inline fun <reified T : Any> getResultState(resultKey: String = T::class.toString()): T? =
        resultStateMap[resultKey]?.value as? T

    inline fun <reified T> setResult(resultKey: String = T::class.toString(), result: T) {
        resultStateMap[resultKey] = mutableStateOf(result)
    }

    inline fun <reified T> removeResult(resultKey: String = T::class.toString()) {
        resultStateMap.remove(resultKey)
    }

    inline fun <reified T : Any> getResultStateAndRemove(resultKey: String = T::class.toString()): T? {
        return (resultStateMap[resultKey]?.value as? T).also { removeResult<T>(resultKey) }
    }
}