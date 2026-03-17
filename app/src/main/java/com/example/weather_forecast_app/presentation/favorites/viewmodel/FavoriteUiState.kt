package com.example.weather_forecast_app.presentation.favorites.viewmodel

import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.utils.UiText

sealed class FavoriteUiState {
    object Loading : FavoriteUiState()
    data class Success(
        val favorites: List<FavoriteLocation>,
        val isOnline: Boolean = true
    ) : FavoriteUiState()
    data class Error(val message: UiText) : FavoriteUiState()
}