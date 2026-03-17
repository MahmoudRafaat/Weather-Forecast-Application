package com.example.weather_forecast_app.data.weather.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.weather_forecast_app.BuildConfig
import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.model.ForecastEntity
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BuildConfig.DATA_STORE_NAME)

class WeatherLocalDataSourceImpl(
    private val weatherDao: WeatherDao,
    private val context: Context
) : WeatherLocalDataSource {


    override fun getCurrentWeather(): Flow<WeatherEntity?> = 
        weatherDao.getCurrentWeather()

    override suspend fun insertCurrentWeather(weather: WeatherEntity) = 
        weatherDao.insertCurrentWeather(weather)

    override fun getForecast(): Flow<ForecastEntity?> = 
        weatherDao.getForecast()

    override suspend fun insertForecast(forecast: ForecastEntity) = 
        weatherDao.insertForecast(forecast)

    // Settings Implementation
    override fun getUnits(): Flow<String> =
        context.dataStore.data.map { it[UNITS_KEY] ?: "metric" }

    override suspend fun setUnits(units: String) {
        context.dataStore.edit { it[UNITS_KEY] = units }
    }

    override fun getWindSpeedUnit(): Flow<String> =
        context.dataStore.data.map { it[WIND_SPEED_UNIT_KEY] ?: "m/s" }

    override suspend fun setWindSpeedUnit(unit: String) {
        context.dataStore.edit { it[WIND_SPEED_UNIT_KEY] = unit }
    }

    override fun getLanguage(): Flow<String> =
        context.dataStore.data.map { it[LANG_KEY] ?: "en" }

    override suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[LANG_KEY] = lang }
    }

    override fun getLocationMode(): Flow<String> =
        context.dataStore.data.map { it[LOCATION_MODE_KEY] ?: "gps" }

    override suspend fun setLocationMode(mode: String) {
        context.dataStore.edit { it[LOCATION_MODE_KEY] = mode }
    }

    override fun getThemeMode(): Flow<String> =
        context.dataStore.data.map { it[THEME_MODE_KEY] ?: "system" }

    override suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[THEME_MODE_KEY] = mode }
    }

    override fun getManualLocation(): Flow<Pair<Double, Double>?> =
        context.dataStore.data.map { preferences ->
            val lat = preferences[MANUAL_LAT_KEY]?.toDoubleOrNull()
            val lon = preferences[MANUAL_LON_KEY]?.toDoubleOrNull()
            if (lat != null && lon != null) lat to lon else null
        }

    override fun getManualCityName(): Flow<String?> =
        context.dataStore.data.map { it[MANUAL_CITY_NAME_KEY] }

    override suspend fun setManualLocation(lat: Double, lon: Double, cityName: String?) {
        context.dataStore.edit { preferences ->
            preferences[MANUAL_LAT_KEY] = lat.toString()
            preferences[MANUAL_LON_KEY] = lon.toString()
            cityName?.let { preferences[MANUAL_CITY_NAME_KEY] = it }
        }
    }

    override fun getOnboardingShown(): Flow<Boolean> =
        context.dataStore.data.map { it[ONBOARDING_KEY] ?: false }

    override suspend fun setOnboardingShown() {
        context.dataStore.edit { it[ONBOARDING_KEY] = true }
    }

    // Favorites Implementation
    override fun getAllFavorites(): Flow<List<FavoriteLocation>> = weatherDao.getAllFavorites()

    override suspend fun insertFavorite(favorite: FavoriteLocation) = weatherDao.insertFavorite(favorite)

    override suspend fun deleteFavorite(favorite: FavoriteLocation) = weatherDao.deleteFavorite(favorite)

    // Alerts Implementation
    override fun getAllAlerts(): Flow<List<WeatherAlert>> = weatherDao.getAllAlerts()

    override suspend fun insertAlert(alert: WeatherAlert): Long = weatherDao.insertAlert(alert)

    override suspend fun deleteAlert(alert: WeatherAlert) = weatherDao.deleteAlert(alert)

    override suspend fun getAlertById(id: Int): WeatherAlert? = weatherDao.getAlertById(id)

    companion object {
        private val UNITS_KEY = stringPreferencesKey("units")
        private val WIND_SPEED_UNIT_KEY = stringPreferencesKey("wind_speed_unit")
        private val LANG_KEY = stringPreferencesKey("lang")
        private val LOCATION_MODE_KEY = stringPreferencesKey("location_mode")
        private val MANUAL_LAT_KEY = stringPreferencesKey("manual_lat")
        private val MANUAL_LON_KEY = stringPreferencesKey("manual_lon")
        private val MANUAL_CITY_NAME_KEY = stringPreferencesKey("manual_city_name")
        private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_shown")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
}
