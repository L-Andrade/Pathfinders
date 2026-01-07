package com.andradel.pathfinders.shared.features.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.activity_number
import pathfinders.shared.generated.resources.participant_number
import pathfinders.shared.generated.resources.participant_score

@Composable
fun TeamInfo(participants: Int, points: Int, activities: Int? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        TeamInfoRow(Icons.Filled.Groups, stringResource(Res.string.participant_number, participants))
        if (activities != null) {
            TeamInfoRow(Icons.Filled.LocalActivity, stringResource(Res.string.activity_number, activities))
        }
        TeamInfoRow(Icons.Filled.Scoreboard, stringResource(Res.string.participant_score, points))
    }
}

@Composable
private fun TeamInfoRow(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}