package com.example.weather_forecast_app.presentation.settings.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast_app.presentation.theme.SolidMagenta

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = SolidMagenta,
        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp),
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}
