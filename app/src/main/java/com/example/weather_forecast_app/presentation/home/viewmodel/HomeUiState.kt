package com.example.weather_forecast_app.presentation.home.viewmodel

import com.example.weather_forecast_app.data.weather.model.ForecastEntity
import com.example.weather_forecast_app.data.weather.model.LocalForecastItem
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import com.example.weather_forecast_app.utils.UiText

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val currentWeather: WeatherEntity,
        val hourlyForecast: List<LocalForecastItem>,
        val dailyForecast: List<LocalForecastItem>,
        val isOnline: Boolean = true,
        val units: String = "metric",
        val windSpeedUnit: String = "m/s",
        val language: String = "en",
        val lastUpdate: String = ""
    ) : HomeUiState()
    data class Error(val message: UiText) : HomeUiState()
}
