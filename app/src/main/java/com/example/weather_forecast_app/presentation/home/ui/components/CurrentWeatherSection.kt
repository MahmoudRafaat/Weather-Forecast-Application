package com.example.weather_forecast_app.presentation.home.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import com.example.weather_forecast_app.utils.WeatherUtils
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherSection(weather: WeatherEntity, lastUpdate: String, tempUnit: String) {
    val displayTime = if (lastUpdate.isNotEmpty()) lastUpdate else WeatherUtils.formatUnixTimestamp(weather.dt, stringResource(R.string.time_format_am_pm))
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val iconRes = WeatherUtils.getWeatherIcon(weather.icon, isBig = true)
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(160.dp),
            tint = Color.Unspecified
        )
        Text(
            text = stringResource(R.string.temperature_format, WeatherUtils.formatNumber(weather.temp.roundToInt()), tempUnit),
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 110.sp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Light
        )
        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.last_update_format, displayTime),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.refresh_for_updates),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
    }
}
