package com.andradel.pathfinders.firebase.messaging

import com.andradel.pathfinders.firebase.functions.UserFunctions
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PathfindersMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var userFunctions: UserFunctions

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        coroutineScope.launch {
            userFunctions.setUserToken(token)
        }
    }
}