package com.andradel.pathfinders.ui.fields

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andradel.pathfinders.R
import androidx.compose.material3.DatePicker as MaterialDatePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    dateRepresentation: String?,
    dateMillis: Long,
    updateDate: (Long) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @StringRes hint: Int = R.string.date,
) {
    var showingDatePicker by remember { mutableStateOf(false) }
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .clickable(enabled = enabled) { showingDatePicker = true }
            .clip(RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp))
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(all = 16.dp),
    ) {
        Text(text = dateRepresentation ?: stringResource(id = hint))
    }
    if (showingDatePicker) {
        DatePicker(
            dateMillis = dateMillis,
            onDateSelected = updateDate,
            onDismiss = { showingDatePicker = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    dateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    datePickerState: DatePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis),
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis ?: return@TextButton)
                onDismiss()
            }) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        modifier = modifier,
    ) {
        MaterialDatePicker(state = datePickerState)
    }
}