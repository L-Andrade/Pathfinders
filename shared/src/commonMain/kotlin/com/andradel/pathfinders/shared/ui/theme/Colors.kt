package com.andradel.pathfinders.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// TODO: Flavours

private val lightYellow = Color(0xFFFFF9C4)
private val yellow = Color(0xFFFFEB3B)
private val darkYellow = Color(0xFFFBC02D)
private val red = Color(0xFFFF5252)

private val darkColorPalette = darkColorScheme(
    primary = red,
    primaryContainer = darkYellow,
    secondary = yellow,
    onSecondary = Color.Black,
)

private val lightColorPalette = lightColorScheme(
    primary = red,
    primaryContainer = lightYellow,
    secondary = yellow,
    onSecondary = Color.Black,
)

// TODO: Dynamic colors?
@Composable
fun colorScheme(darkTheme: Boolean): ColorScheme {
    // val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
        // dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        // dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> darkColorPalette
        else -> lightColorPalette
    }
    return colors
}