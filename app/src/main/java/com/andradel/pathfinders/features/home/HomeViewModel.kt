package com.andradel.pathfinders.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andradel.pathfinders.R
import com.andradel.pathfinders.firebase.participant.ParticipantFirebaseDataSource
import com.andradel.pathfinders.user.User
import com.andradel.pathfinders.user.UserSession
import com.andradel.pathfinders.user.UserState
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userSession: UserSession,
    private val dataSource: ParticipantFirebaseDataSource
) : ViewModel() {
    val state = userSession.userState.map { state ->
        when (state) {
            UserState.Guest -> HomeState.Guest
            UserState.Loading -> HomeState.Loading
            is User -> HomeState.Loaded(
                participant = if (state.email != null) dataSource.participantByEmail(state.email) else null,
                user = state
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState.Loading)

    fun onSignInResult(res: FirebaseAuthUIAuthenticationResult?) {
        viewModelScope.launch {
            userSession.updateUser()
        }
    }

    fun onSignOutClick() {
        userSession.signOut()
    }

    val signInIntent = run {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            // AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_Pathfinders)
            .build()
    }
}