package com.example.weather_forecast_app.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute {
    @Serializable
    object SplashScreenRoute : ScreenRoute()
    @Serializable
    object HomeScreenRoute : ScreenRoute()
    @Serializable
    object SettingsScreenRoute : ScreenRoute()
    @Serializable
    data class MapScreenRoute(val isFavoriteMode: Boolean = false) : ScreenRoute()
    @Serializable
    object FavoriteScreenRoute : ScreenRoute()
    @Serializable
    data class FavoriteDetailsScreenRoute(
        val lat: Double,
        val lon: Double,
        val cityName: String
    ) : ScreenRoute()
    @Serializable
    object AlertsScreenRoute : ScreenRoute()
}
