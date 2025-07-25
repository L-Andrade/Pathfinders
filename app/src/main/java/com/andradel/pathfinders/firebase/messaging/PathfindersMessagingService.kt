package com.andradel.pathfinders.firebase.messaging

import com.andradel.pathfinders.firebase.functions.UserFunctions
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class PathfindersMessagingService : FirebaseMessagingService(), KoinComponent {

    private val coroutineScope: CoroutineScope by inject()
    private val userFunctions: UserFunctions by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        coroutineScope.launch {
            userFunctions.setUserToken(token)
        }
    }
}