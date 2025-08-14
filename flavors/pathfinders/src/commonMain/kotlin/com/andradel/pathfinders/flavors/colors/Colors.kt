package com.andradel.pathfinders.flavors.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val lightYellow = Color(0xFFFFF9C4)
private val yellow = Color(0xFFFFEB3B)
private val darkYellow = Color(0xFFFBC02D)
private val red = Color(0xFFFF5252)

val darkColorPalette = darkColorScheme(
    primary = red,
    primaryContainer = darkYellow,
    secondary = yellow,
    onSecondary = Color.Black,
)

val lightColorPalette = lightColorScheme(
    primary = red,
    primaryContainer = lightYellow,
    secondary = yellow,
    onSecondary = Color.Black,
)