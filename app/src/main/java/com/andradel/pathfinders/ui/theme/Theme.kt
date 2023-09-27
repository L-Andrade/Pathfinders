package com.andradel.pathfinders.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val lightYellow = Color(0xFFFFF9C4)
private val yellow = Color(0xFFFFEB3B)
private val darkYellow = Color(0xFFFBC02D)
private val red = Color(0xFFFF5252)

private val darkColorPalette = darkColorScheme(
    primary = red,
    primaryContainer = darkYellow,
    secondary = yellow,
)

private val lightColorPalette = lightColorScheme(
    primary = red,
    primaryContainer = lightYellow,
    secondary = yellow,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun PathfindersTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> darkColorPalette
        else -> lightColorPalette
    }


    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}