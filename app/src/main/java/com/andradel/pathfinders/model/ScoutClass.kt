package com.andradel.pathfinders.model

import androidx.annotation.Keep
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.andradel.pathfinders.R

@Keep
enum class ScoutClass {
    Rebento,
    Ticao,
    Explorador,
    Companheiro,
    Embaixador,
    Invalid;

    companion object {
        val options = ScoutClass.values().toList() - Invalid
    }
}

val ScoutClass.color: Color
    @Composable get() = when (this) {
        ScoutClass.Rebento -> Color.Green
        ScoutClass.Ticao -> Color.Red
        ScoutClass.Explorador -> Color.Yellow
        ScoutClass.Companheiro -> Color.Blue
        ScoutClass.Embaixador -> Color(0xFFb83404)
        ScoutClass.Invalid -> MaterialTheme.colors.primary
    }

val ScoutClass.title: String
    @Composable get() = when (this) {
        ScoutClass.Rebento -> R.string.rebento
        ScoutClass.Ticao -> R.string.ticao
        ScoutClass.Explorador -> R.string.explorador
        ScoutClass.Companheiro -> R.string.companheiro
        ScoutClass.Embaixador -> R.string.embaixador
        ScoutClass.Invalid -> R.string.invalid_class
    }.let { stringResource(id = it) }