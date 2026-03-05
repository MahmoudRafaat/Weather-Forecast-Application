package com.example.weather_forecast_app.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute() {
    @Serializable
    object SplashScreenRoute : ScreenRoute()
    @Serializable
    object HomeScreenRoute : ScreenRoute()
}
