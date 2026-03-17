package com.example.weather_forecast_app.data.weather.model

data class HourlyForecast(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Item0>,
    val message: Int
)