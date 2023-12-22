package com.andradel.pathfinders.model

import androidx.annotation.Keep
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.andradel.pathfinders.R

@Keep
enum class ParticipantClass {
    Juvenil,
    Jovem,
    TrintaMais,
    Invalid;

    companion object {
        val options = entries - Invalid

        val last = TrintaMais
    }
}

val ParticipantClass.color: Color
    @Composable get() = MaterialTheme.colorScheme.primary

val ParticipantClass.title: String
    @Composable get() = when (this) {
        ParticipantClass.Juvenil -> R.string.juvenil
        ParticipantClass.Jovem -> R.string.jovem
        ParticipantClass.TrintaMais -> R.string.trinta_mais
        ParticipantClass.Invalid -> R.string.invalid_class
    }.let { stringResource(id = it) }