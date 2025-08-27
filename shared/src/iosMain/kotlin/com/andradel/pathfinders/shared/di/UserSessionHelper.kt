package com.andradel.pathfinders.shared.di

import com.andradel.pathfinders.shared.user.UserSession
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserSessionHelper : KoinComponent {
    val userSession: UserSession by inject()
}