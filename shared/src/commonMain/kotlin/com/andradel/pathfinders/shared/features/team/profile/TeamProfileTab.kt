package com.andradel.pathfinders.shared.features.team.profile

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.activity_list
import pathfinders.shared.generated.resources.participant_list

enum class TeamProfileTab {
    Participants,
    Activities,
}

val TeamProfileTab.title: String
    @Composable
    get() = when (this) {
        TeamProfileTab.Participants -> stringResource(Res.string.participant_list)
        TeamProfileTab.Activities -> stringResource(Res.string.activity_list)
    }