package com.example.weather_forecast_app.presentation.home.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import com.example.weather_forecast_app.utils.WeatherUtils
import kotlin.math.roundToInt

@Composable
fun WeatherDetailsGrid(weather: WeatherEntity, windSpeedUnit: String, currentSystem: String) {
    val convertedWindSpeed = WeatherUtils.convertWindSpeed(weather.windSpeed, windSpeedUnit, currentSystem)
    val windUnitLabel = if (windSpeedUnit == "m/s") stringResource(R.string.unit_m_s) else stringResource(R.string.unit_mile_h)

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherDetailItem(
                label = stringResource(R.string.humidity),
                value = WeatherUtils.formatNumber(weather.humidity),
                unit = stringResource(R.string.unit_percentage),
                icon = painterResource(id = R.drawable.humidity),
                modifier = Modifier.weight(1f)
            )
            WeatherDetailItem(
                label = stringResource(R.string.wind_speed),
                value = WeatherUtils.formatNumber(convertedWindSpeed.roundToInt()),
                unit = windUnitLabel,
                icon = painterResource(id = R.drawable.wind_speed),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherDetailItem(
                label = stringResource(R.string.pressure),
                value = WeatherUtils.formatNumber(weather.pressure),
                unit = stringResource(R.string.unit_hpa),
                icon = painterResource(id = R.drawable.pressure),
                modifier = Modifier.weight(1f)
            )
            WeatherDetailItem(
                label = stringResource(R.string.cloudiness),
                value = WeatherUtils.formatNumber(weather.clouds),
                unit = stringResource(R.string.unit_percentage),
                icon = painterResource(id = R.drawable.cloudiness),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
