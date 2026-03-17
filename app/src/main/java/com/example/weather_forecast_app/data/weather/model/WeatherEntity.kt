package com.example.weather_forecast_app.data.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val id: Int = 0,
    val cityName: String,
    val temp: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Int,
    val clouds: Int,
    val dt: Int
)
