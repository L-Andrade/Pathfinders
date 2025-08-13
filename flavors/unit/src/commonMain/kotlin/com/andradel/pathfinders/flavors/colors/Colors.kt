package com.andradel.pathfinders.flavors.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val gray = Color(0xFF607d8b)
private val blue = Color(0xFF3f51b5)
private val lightBlue = Color(0xFFC5CAE9)
private val darkBlue = Color(0xFF3F51B5)

private val darkColorPalette = darkColorScheme(
    primary = blue,
    primaryContainer = darkBlue,
    secondary = gray,
)

private val lightColorPalette = lightColorScheme(
    primary = blue,
    primaryContainer = lightBlue,
    secondary = gray,
)

// TODO
// @Composable
// fun colorScheme(darkTheme: Boolean): ColorScheme {
//     val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
//     val colors = when {
//         dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
//         dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
//         darkTheme -> darkColorPalette
//         else -> lightColorPalette
//     }
//     return colors
// }