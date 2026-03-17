package com.example.weather_forecast_app.presentation.settings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.weather_forecast_app.presentation.theme.WeatherTheme

@Composable
fun SettingsCard(enabled: Boolean = true, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.5f)
            .background(WeatherTheme.colors.cardBackground, RoundedCornerShape(24.dp))
            .border(1.dp, WeatherTheme.colors.cardBorder, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp)),
        content = content
    )
}
