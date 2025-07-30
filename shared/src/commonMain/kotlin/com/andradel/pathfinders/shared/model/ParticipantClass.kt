package com.andradel.pathfinders.shared.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.class_unknown
import pathfinders.shared.generated.resources.companheiro
import pathfinders.shared.generated.resources.embaixador
import pathfinders.shared.generated.resources.explorador
import pathfinders.shared.generated.resources.invalid_class
import pathfinders.shared.generated.resources.rebento
import pathfinders.shared.generated.resources.ticao

// TODO: Flavours
enum class ParticipantClass {
    Rebento,
    Ticao,
    Explorador,
    Companheiro,
    Embaixador,
    Invalid,
    Unknown,
    ;

    companion object {
        val options = entries - Invalid - Unknown

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
        ParticipantClass.Invalid, ParticipantClass.Unknown -> MaterialTheme.colorScheme.primary
    }

val ParticipantClass.title: String
    @Composable get() = when (this) {
        ParticipantClass.Rebento -> Res.string.rebento
        ParticipantClass.Ticao -> Res.string.ticao
        ParticipantClass.Explorador -> Res.string.explorador
        ParticipantClass.Companheiro -> Res.string.companheiro
        ParticipantClass.Embaixador -> Res.string.embaixador
        ParticipantClass.Invalid -> Res.string.invalid_class
        ParticipantClass.Unknown -> Res.string.class_unknown
    }.let { stringResource(it) }