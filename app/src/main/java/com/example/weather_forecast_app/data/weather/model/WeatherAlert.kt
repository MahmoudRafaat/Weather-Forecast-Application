package com.example.weather_forecast_app.data.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val type: String, // "alarm" or "notification"
    val isEnabled: Boolean = true
)
