package com.andradel.pathfinders.shared.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

fun <T> NavController.navigateBackWithResult(key: String, value: T?) {
    previousBackStackEntry?.savedStateHandle?.set(key, value)
    navigateUp()
}

@Composable
fun <T> NavController.collectNavResultAsState(key: String): State<T?> = this.currentBackStackEntry
    ?.savedStateHandle
    ?.removeValueAndCollectAsState(key)
    ?: remember(this, key) { mutableStateOf(null) }

@Composable
private fun <T> SavedStateHandle.removeValueAndCollectAsState(key: String): State<T?> =
    getMutableStateFlow<T?>(key, null).collectAsStateWithLifecycle().also { remove<T>(key) }