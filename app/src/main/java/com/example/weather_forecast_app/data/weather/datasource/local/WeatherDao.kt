package com.example.weather_forecast_app.data.weather.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.model.ForecastEntity
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE id = 0")
     fun getCurrentWeather(): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather: WeatherEntity)

    @Query("SELECT * FROM forecast WHERE id = 0")
    fun getForecast(): Flow<ForecastEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastEntity)

    // Favorites
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteLocation)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteLocation)

    // Alerts
    @Query("SELECT * FROM weather_alerts")
    fun getAllAlerts(): Flow<List<WeatherAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: WeatherAlert): Long

    @Delete
    suspend fun deleteAlert(alert: WeatherAlert)
    
    @Query("SELECT * FROM weather_alerts WHERE id = :id")
    suspend fun getAlertById(id: Int): WeatherAlert?
}
