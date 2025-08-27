package com.andradel.pathfinders.shared.intents

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCallIntents(): CallIntents

expect class CallIntents {
    fun onCall(phoneNumber: String)
}