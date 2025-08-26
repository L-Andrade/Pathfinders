package com.andradel.pathfinders.flavors.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import pathfinders.flavors.unit.generated.resources.Res
import pathfinders.flavors.unit.generated.resources.a_mais
import pathfinders.flavors.unit.generated.resources.adolescentes
import pathfinders.flavors.unit.generated.resources.agape
import pathfinders.flavors.unit.generated.resources.class_unknown
import pathfinders.flavors.unit.generated.resources.discipulos
import pathfinders.flavors.unit.generated.resources.mensageiros
import pathfinders.flavors.unit.generated.resources.remanescente
import pathfinders.flavors.unit.generated.resources.trinta_mais
import pathfinders.flavors.unit.generated.resources.unidos_em_cristo

enum class ParticipantClass {
    UA1,
    UA2,
    UA3,
    UA4,
    UA5,
    UA6,

    // UA7
    Invalid,

    UA8,
    Unknown,
    ;

    companion object {
        val options = entries - Unknown

        val last = Invalid
    }
}

val ParticipantClass.color: Color
    @Composable get() = MaterialTheme.colorScheme.primary

val ParticipantClass.title: String
    @Composable get() = when (this) {
        ParticipantClass.UA1 -> Res.string.remanescente
        ParticipantClass.UA2 -> Res.string.mensageiros
        ParticipantClass.UA3 -> Res.string.discipulos
        ParticipantClass.UA4 -> Res.string.unidos_em_cristo
        ParticipantClass.UA5 -> Res.string.agape
        ParticipantClass.UA6 -> Res.string.trinta_mais
        ParticipantClass.UA8 -> Res.string.a_mais
        // TODO: Next version, we need to properly use invalid, and most likely classes that are not hardcoded
        ParticipantClass.Invalid -> Res.string.adolescentes
        ParticipantClass.Unknown -> Res.string.class_unknown
    }.let { stringResource(it) }