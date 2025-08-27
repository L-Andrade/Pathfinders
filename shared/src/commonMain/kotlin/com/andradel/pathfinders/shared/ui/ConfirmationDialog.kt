package com.andradel.pathfinders.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.stringResource
import pathfinders.shared.generated.resources.Res
import pathfinders.shared.generated.resources.no
import pathfinders.shared.generated.resources.yes

@Composable
fun ConfirmationDialog(title: String, body: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            Text(text = body, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.size(16.dp))
            Row(modifier = Modifier.align(Alignment.End)) {
                OutlinedButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.no))
                }
                Spacer(modifier = Modifier.size(4.dp))
                Button(onClick = onConfirm) {
                    Text(stringResource(Res.string.yes))
                }
            }
        }
    }
}