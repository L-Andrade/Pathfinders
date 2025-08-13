package com.andradel.pathfinders.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun colorScheme(darkTheme: Boolean): ColorScheme {
    val colors = when {
        darkTheme -> darkColorPalette
        else -> lightColorPalette
    }
    return colors
}