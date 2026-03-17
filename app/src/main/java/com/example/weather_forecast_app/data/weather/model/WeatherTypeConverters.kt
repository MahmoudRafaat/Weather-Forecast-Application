package com.example.weather_forecast_app.data.weather.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromForecastList(value: List<LocalForecastItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toForecastList(value: String): List<LocalForecastItem> {
        val listType = object : TypeToken<List<LocalForecastItem>>() {}.type
        return gson.fromJson(value, listType)
    }
}
