package com.andradel.pathfinders.model

import androidx.annotation.Keep
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.andradel.pathfinders.R

@Keep
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
        ParticipantClass.UA1 -> R.string.remanescente
        ParticipantClass.UA2 -> R.string.mensageiros
        ParticipantClass.UA3 -> R.string.discipulos
        ParticipantClass.UA4 -> R.string.unidos_em_cristo
        ParticipantClass.UA5 -> R.string.agape
        ParticipantClass.UA6 -> R.string.trinta_mais
        ParticipantClass.UA8 -> R.string.a_mais
        // TODO: Next version, we need to properly use invalid, and most likely classes that are not hardcoded
        ParticipantClass.Invalid -> R.string.adolescentes
        ParticipantClass.Unknown -> R.string.class_unknown
    }.let { stringResource(id = it) }