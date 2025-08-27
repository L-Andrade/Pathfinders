package com.andradel.pathfinders.shared

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(onSignInClick: () -> Unit) = ComposeUIViewController {
    Pathfinders(onSignInClick)
}