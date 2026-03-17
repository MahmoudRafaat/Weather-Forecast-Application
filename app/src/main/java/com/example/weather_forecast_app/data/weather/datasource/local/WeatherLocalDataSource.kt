package com.example.weather_forecast_app.data.weather.datasource.local

import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.model.ForecastEntity
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getCurrentWeather(): Flow<WeatherEntity?>
    suspend fun insertCurrentWeather(weather: WeatherEntity)
    fun getForecast(): Flow<ForecastEntity?>
    suspend fun insertForecast(forecast: ForecastEntity)

    // Settings
    fun getUnits(): Flow<String>
    suspend fun setUnits(units: String)
    fun getWindSpeedUnit(): Flow<String>
    suspend fun setWindSpeedUnit(unit: String)
    fun getLanguage(): Flow<String>
    suspend fun setLanguage(lang: String)
    fun getLocationMode(): Flow<String>
    suspend fun setLocationMode(mode: String)
    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)
    fun getManualLocation(): Flow<Pair<Double, Double>?>
    fun getManualCityName(): Flow<String?>
    suspend fun setManualLocation(lat: Double, lon: Double, cityName: String?)
    fun getOnboardingShown(): Flow<Boolean>
    suspend fun setOnboardingShown()

    // Favorites
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
    suspend fun insertFavorite(favorite: FavoriteLocation)
    suspend fun deleteFavorite(favorite: FavoriteLocation)

    // Alerts
    fun getAllAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertAlert(alert: WeatherAlert): Long
    suspend fun deleteAlert(alert: WeatherAlert)
    suspend fun getAlertById(id: Int): WeatherAlert?
}
