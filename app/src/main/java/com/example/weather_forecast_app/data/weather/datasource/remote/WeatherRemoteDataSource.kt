package com.example.weather_forecast_app.data.weather.datasource.remote

import com.example.weather_forecast_app.data.weather.model.HourlyForecast
import com.example.weather_forecast_app.data.weather.model.WeatherModel
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String, lang: String): Response<WeatherModel>
    suspend fun getHourlyForecast(lat: Double, lon: Double, units: String, lang: String): Response<HourlyForecast>
}
