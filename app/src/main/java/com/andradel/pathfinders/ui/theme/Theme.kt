package com.andradel.pathfinders.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun PathfindersTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colorScheme(darkTheme),
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}