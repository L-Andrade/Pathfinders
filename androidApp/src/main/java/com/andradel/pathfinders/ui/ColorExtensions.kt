package com.andradel.pathfinders.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val Color.onColor: Color
    @Composable get() = if (luminance() > 0.5f) Color.Black else Color.White