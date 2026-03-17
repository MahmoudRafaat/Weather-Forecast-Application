package com.example.weather_forecast_app.presentation.alerts.viewmodel

import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.utils.UiText

sealed class AlertUiState {
    object Loading : AlertUiState()
    data class Success(val alerts: List<WeatherAlert>) : AlertUiState()
    data class Error(val message: UiText) : AlertUiState()
}