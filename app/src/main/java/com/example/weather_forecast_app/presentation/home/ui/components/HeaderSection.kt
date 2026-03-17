package com.example.weather_forecast_app.presentation.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import com.example.weather_forecast_app.utils.WeatherUtils

@Composable
fun HeaderSection(weather: WeatherEntity) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = weather.cityName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = WeatherUtils.formatUnixTimestamp(weather.dt, stringResource(R.string.date_format_full)),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}
