package com.andradel.pathfinders.shared.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.shared.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.shared.user.User
import com.andradel.pathfinders.shared.user.UserSession
import com.andradel.pathfinders.shared.user.UserState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val userSession: UserSession,
    private val dataSource: ParticipantFirebaseDataSource,
) : ViewModel() {
    val state = userSession.userState.map { state ->
        when (state) {
            UserState.Guest -> HomeState.Guest
            UserState.Loading -> HomeState.Loading
            is User -> HomeState.Loaded(
                participant = if (state.email != null) dataSource.participantByEmail(null, state.email) else null,
                user = state,
            )

            UserState.Error -> HomeState.Error
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState.Loading)

    fun updateUser() {
        userSession.updateUser()
    }

    fun onSignOutClick() {
        userSession.signOut()
    }
}