package com.andradel.pathfinders.shared.intents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun rememberCallIntents(): CallIntents {
    return remember { CallIntents() }
}

actual class CallIntents {
    actual fun onCall(phoneNumber: String) {
        val url = NSURL(string = "tel://$phoneNumber")
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}