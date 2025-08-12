package com.andradel.pathfinders.shared.intents

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
actual fun rememberCallIntents(): CallIntents {
    val context = LocalContext.current
    return remember(context) { CallIntents(context) }
}

actual class CallIntents(private val context: Context) {
    actual fun onCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply { data = "tel:${phoneNumber}".toUri() }
        context.startActivity(intent)
    }
}