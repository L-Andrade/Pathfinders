package com.andradel.pathfinders.ui

import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.andradel.pathfinders.R
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DatePicker(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedDate by remember { mutableStateOf(date) }
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = modifier) {
            Column {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colors.primary)
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = stringResource(id = R.string.select_date),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onPrimary
                    )
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("MMM d, YYYY")),
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
                AndroidCalendarView(selectedDate = selectedDate, onDateSelected = { selectedDate = it })
                Row(modifier = Modifier.padding(bottom = 16.dp, end = 16.dp).align(Alignment.End)) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            onDateSelected(selectedDate)
                            onDismiss()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.ok))
                    }

                }
            }
        }
    }
}

@Composable
private fun AndroidCalendarView(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    AndroidView(
        factory = { context -> CalendarView(context) },
        update = { view ->
            view.date = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                onDateSelected(
                    LocalDate
                        .now()
                        .withMonth(month + 1)
                        .withYear(year)
                        .withDayOfMonth(dayOfMonth)
                )
            }
        }
    )
}