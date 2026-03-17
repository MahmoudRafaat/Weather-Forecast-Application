package com.example.weather_forecast_app.presentation.home.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.weather.model.LocalForecastItem
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import com.example.weather_forecast_app.utils.WeatherUtils

@Composable
fun HomeContent(
    currentWeather: WeatherEntity,
    hourlyForecast: List<LocalForecastItem>,
    dailyForecast: List<LocalForecastItem>,
    lastUpdate: String,
    windSpeedUnit: String = "m/s",
    currentSystem: String = "metric"
) {
    val todayDateStr = WeatherUtils.formatUnixTimestamp(currentWeather.dt, "yyyy-MM-dd")
    
    val filteredDaily = dailyForecast.filter {
        WeatherUtils.formatUnixTimestamp(it.dt, "yyyy-MM-dd") != todayDateStr 
    }
    
    val next24Hours = hourlyForecast.take(8)
    
    val tempUnit = when(currentSystem) {
        "metric" -> stringResource(R.string.unit_celsius)
        "imperial" -> stringResource(R.string.unit_fahrenheit)
        "standard" -> stringResource(R.string.unit_kelvin)
        else -> "°"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection(currentWeather)
        Spacer(modifier = Modifier.height(32.dp))
        CurrentWeatherSection(currentWeather, lastUpdate, tempUnit)
        Spacer(modifier = Modifier.height(40.dp))
        ForecastSection(
            title = stringResource(R.string.today),
            items = next24Hours,
            dateFormat = stringResource(R.string.time_format_am_pm),
            tempUnit = tempUnit
        )
        Spacer(modifier = Modifier.height(24.dp))
        WeatherDetailsGrid(currentWeather, windSpeedUnit, currentSystem)
        Spacer(modifier = Modifier.height(24.dp))
        ForecastSection(
            title = stringResource(R.string.next_days),
            items = filteredDaily,
            dateFormat = stringResource(R.string.date_format_short_day),
            tempUnit = tempUnit
        )
    }
}
