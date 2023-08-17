package com.andradel.pathfinders.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andradel.pathfinders.R


@Composable
fun ConfirmationDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(text = body, color = MaterialTheme.colors.onBackground)
            Spacer(modifier = Modifier.size(16.dp))
            Row(modifier = Modifier.align(Alignment.End)) {
                OutlinedButton(onClick = onDismiss) {
                    Text(stringResource(id = R.string.no))
                }
                Spacer(modifier = Modifier.size(4.dp))
                Button(onClick = onConfirm) {
                    Text(stringResource(id = R.string.yes))
                }
            }
        }
    }
}