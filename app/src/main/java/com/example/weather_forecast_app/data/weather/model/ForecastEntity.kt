package com.example.weather_forecast_app.data.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey val id: Int = 0,
    val hourlyList: List<LocalForecastItem>,
    val dailyList: List<LocalForecastItem>
)

data class LocalForecastItem(
    val dt: Int,
    val temp: Double,
    val icon: String
)
