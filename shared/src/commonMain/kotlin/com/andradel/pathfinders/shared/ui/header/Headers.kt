package com.andradel.pathfinders.shared.ui.header

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.ic_add
import pathfinders.shared.generated.resources.select

@Composable
fun HeaderWithAddButton(header: String, onClick: () -> Unit, showButton: Boolean, modifier: Modifier = Modifier) {
    Header(header = header, modifier = modifier) {
        if (showButton) {
            OutlinedButton(onClick = onClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = stringResource(Res.string.select),
                    )
                    Text(text = stringResource(Res.string.select))
                }
            }
        }
    }
}

@Composable
fun Header(header: String, modifier: Modifier = Modifier, endContent: @Composable () -> Unit = {}) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = header, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        endContent()
    }
}