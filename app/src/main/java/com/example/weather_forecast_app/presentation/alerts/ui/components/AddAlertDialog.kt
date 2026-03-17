package com.example.weather_forecast_app.presentation.alerts.ui.components

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.presentation.theme.SolidMagenta
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit,
    onSave: (startTime: Long, endTime: Long, type: String) -> Unit
) {
    var startTime by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    var endTime by remember { mutableStateOf(Calendar.getInstance().timeInMillis + 3600000) }
    var type by remember { mutableStateOf("notification") }
    var errorResId by remember { mutableStateOf<Int?>(null) }

    val context = LocalContext.current
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_alert), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Start Time Picker
                OutlinedCard(
                    onClick = {
                        showTimePicker(context) { calendar ->
                            startTime = calendar.timeInMillis
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = SolidMagenta)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(stringResource(R.string.start_time), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(timeFormat.format(Date(startTime)), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // End Time Picker
                OutlinedCard(
                    onClick = {
                        showTimePicker(context) { calendar ->
                            endTime = calendar.timeInMillis
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = SolidMagenta)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(stringResource(R.string.end_time), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(timeFormat.format(Date(endTime)), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Type Toggle
                Column {
                    Text(stringResource(R.string.notification_type), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = type == "notification",
                            onClick = { type = "notification" },
                            colors = RadioButtonDefaults.colors(selectedColor = SolidMagenta)
                        )
                        Text(stringResource(R.string.notification), modifier = Modifier.clickable { type = "notification" })
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = type == "alarm",
                            onClick = { type = "alarm" },
                            colors = RadioButtonDefaults.colors(selectedColor = SolidMagenta)
                        )
                        Text(stringResource(R.string.alarm), modifier = Modifier.clickable { type = "alarm" })
                    }
                }

                errorResId?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val now = System.currentTimeMillis()
                    if (startTime < now || endTime <= startTime) {
                        errorResId = R.string.invalid_time_error
                    } else {
                        onSave(startTime, endTime, type)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SolidMagenta),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.save), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

private fun showTimePicker(context: Context, onTimeSelected: (Calendar) -> Unit) {
    val currentCalendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onTimeSelected(selectedCalendar)
        },
        currentCalendar.get(Calendar.HOUR_OF_DAY),
        currentCalendar.get(Calendar.MINUTE),
        false
    ).show()
}
