package com.example.weather_forecast_app.data.weather.repository

import com.example.weather_forecast_app.data.weather.datasource.local.WeatherLocalDataSource
import com.example.weather_forecast_app.data.weather.datasource.remote.WeatherRemoteDataSource
import com.example.weather_forecast_app.data.weather.model.*
import kotlinx.coroutines.flow.Flow
import java.io.IOException


class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : WeatherRepository {

    override fun getCurrentWeatherLocal(): Flow<WeatherEntity?> = localDataSource.getCurrentWeather()

    override fun getForecastLocal(): Flow<ForecastEntity?> = localDataSource.getForecast()

    override suspend fun refreshWeather(lat: Double, lon: Double, units: String, lang: String, customCityName: String?) {
        try {
            val weatherResponse = remoteDataSource.getCurrentWeather(lat, lon, units, lang)
            if (weatherResponse.isSuccessful) {
                weatherResponse.body()?.let { 
                    localDataSource.insertCurrentWeather(it.toEntity(customCityName))
                }
            } else {
                throw IOException(weatherResponse.code().toString())
            }

            val forecastResponse = remoteDataSource.getHourlyForecast(lat, lon, units, lang)
            if (forecastResponse.isSuccessful) {
                forecastResponse.body()?.let { 
                    localDataSource.insertForecast(it.toEntity())
                }
            } else {
                throw IOException(forecastResponse.code().toString())
            }
        } catch (e: Exception) {
            throw e
        }
    }

     fun WeatherModel.toEntity(customCityName: String? = null): WeatherEntity {
        return WeatherEntity(
            id = 0,
            cityName = customCityName ?: this.name,
            temp = this.main.temp,
            description = this.weather[0].description,
            icon = this.weather[0].icon,
            humidity = this.main.humidity,
            windSpeed = this.wind.speed,
            pressure = this.main.pressure,
            clouds = this.clouds.all,
            dt = this.dt
        )
    }

     fun HourlyForecast.toEntity(): ForecastEntity {
        val hourly = this.list.take(24).map { 
            LocalForecastItem(it.dt, it.main.temp, it.weather[0].icon)
        }
        val daily = this.list.filterIndexed { index, _ -> index % 8 == 0 }.map { 
            LocalForecastItem(it.dt, it.main.temp, it.weather[0].icon)
        }
        return ForecastEntity(
            id = 0,
            hourlyList = hourly,
            dailyList = daily
        )
    }

    // Settings
    override fun getUnits(): Flow<String> = localDataSource.getUnits()
    override suspend fun setUnits(units: String) { localDataSource.setUnits(units) }

    override fun getWindSpeedUnit(): Flow<String> = localDataSource.getWindSpeedUnit()
    override suspend fun setWindSpeedUnit(unit: String) { localDataSource.setWindSpeedUnit(unit) }

    override fun getLanguage(): Flow<String> = localDataSource.getLanguage()
    override suspend fun setLanguage(lang: String) { localDataSource.setLanguage(lang) }

    override fun getLocationMode(): Flow<String> = localDataSource.getLocationMode()
    override suspend fun setLocationMode(mode: String) { localDataSource.setLocationMode(mode) }

    override fun getThemeMode(): Flow<String> = localDataSource.getThemeMode()
    override suspend fun setThemeMode(mode: String) { localDataSource.setThemeMode(mode) }

    override fun getManualLocation(): Flow<Pair<Double, Double>?> = localDataSource.getManualLocation()
    override fun getManualCityName(): Flow<String?> = localDataSource.getManualCityName()
    override suspend fun setManualLocation(lat: Double, lon: Double, cityName: String?) { localDataSource.setManualLocation(lat, lon, cityName) }

    override fun getOnboardingShown(): Flow<Boolean> = localDataSource.getOnboardingShown()
    override suspend fun setOnboardingShown() { localDataSource.setOnboardingShown() }

    // Favorites
    override fun getAllFavorites(): Flow<List<FavoriteLocation>> = localDataSource.getAllFavorites()
    override suspend fun insertFavorite(favorite: FavoriteLocation) { localDataSource.insertFavorite(favorite) }
    override suspend fun deleteFavorite(favorite: FavoriteLocation) { localDataSource.deleteFavorite(favorite) }
    override suspend fun fetchWeatherForLocation(lat: Double, lon: Double, units: String, lang: String): WeatherModel? {
        return try {
            val response = remoteDataSource.getCurrentWeather(lat, lon, units, lang)
            if (response.isSuccessful) response.body() else throw IOException(response.code().toString())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun fetchForecastForLocation(lat: Double, lon: Double, units: String, lang: String): HourlyForecast? {
        return try {
            val response = remoteDataSource.getHourlyForecast(lat, lon, units, lang)
            if (response.isSuccessful) response.body() else throw IOException(response.code().toString())
        } catch (e: Exception) {
            throw e
        }
    }
    // Alerts
    override fun getAllAlerts(): Flow<List<WeatherAlert>> = localDataSource.getAllAlerts()

    override suspend fun insertAlert(alert: WeatherAlert): Long = localDataSource.insertAlert(alert)

    override suspend fun deleteAlert(alert: WeatherAlert) { localDataSource.deleteAlert(alert) }

    override suspend fun getAlertById(id: Int): WeatherAlert? = localDataSource.getAlertById(id)
}
