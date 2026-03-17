package com.example.weather_forecast_app.data.weather.repository

import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.model.ForecastEntity
import com.example.weather_forecast_app.data.weather.model.HourlyForecast
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import com.example.weather_forecast_app.data.weather.model.WeatherModel
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    //home
    fun getCurrentWeatherLocal(): Flow<WeatherEntity?>
    fun getForecastLocal(): Flow<ForecastEntity?>
    suspend fun refreshWeather(lat: Double, lon: Double, units: String, lang: String, customCityName: String? = null)

    // settings
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

    // favortes
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
    suspend fun insertFavorite(favorite: FavoriteLocation)
    suspend fun deleteFavorite(favorite: FavoriteLocation)
    suspend fun fetchWeatherForLocation(lat: Double, lon: Double, units: String, lang: String): WeatherModel?
    suspend fun fetchForecastForLocation(lat: Double, lon: Double, units: String, lang: String): HourlyForecast?

    // alerts
    fun getAllAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertAlert(alert: WeatherAlert): Long
    suspend fun deleteAlert(alert: WeatherAlert)
    suspend fun getAlertById(id: Int): WeatherAlert?
}
