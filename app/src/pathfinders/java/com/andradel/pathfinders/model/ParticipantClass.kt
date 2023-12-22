package com.andradel.pathfinders.model

import androidx.annotation.Keep
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.andradel.pathfinders.R

@Keep
enum class ParticipantClass {
    Rebento,
    Ticao,
    Explorador,
    Companheiro,
    Embaixador,
    Invalid;

    companion object {
        val options = entries - Invalid

        val last = Embaixador
    }
}

val ParticipantClass.color: Color
    @Composable get() = when (this) {
        ParticipantClass.Rebento -> Color.Green
        ParticipantClass.Ticao -> Color.Red
        ParticipantClass.Explorador -> Color.Yellow
        ParticipantClass.Companheiro -> Color.Blue
        ParticipantClass.Embaixador -> Color(0xFFb83404)
        ParticipantClass.Invalid -> MaterialTheme.colorScheme.primary
    }

val ParticipantClass.title: String
    @Composable get() = when (this) {
        ParticipantClass.Rebento -> R.string.rebento
        ParticipantClass.Ticao -> R.string.ticao
        ParticipantClass.Explorador -> R.string.explorador
        ParticipantClass.Companheiro -> R.string.companheiro
        ParticipantClass.Embaixador -> R.string.embaixador
        ParticipantClass.Invalid -> R.string.invalid_class
    }.let { stringResource(id = it) }