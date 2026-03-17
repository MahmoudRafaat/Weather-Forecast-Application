package com.example.weather_forecast_app.data.weather.datasource.remote

import com.example.weather_forecast_app.data.weather.model.HourlyForecast
import com.example.weather_forecast_app.data.weather.model.WeatherModel
import retrofit2.Response


class WeatherRemoteDataSourceImpl(private val weatherService: WeatherService) : WeatherRemoteDataSource {
    override suspend fun getCurrentWeather(lat: Double, lon: Double, units: String, lang: String): Response<WeatherModel> =
        weatherService.getCurrentWeather(lat, lon, units, lang)

    override suspend fun getHourlyForecast(lat: Double, lon: Double, units: String, lang: String): Response<HourlyForecast> =
        weatherService.getHourlyForecast(lat, lon, units, lang)
}
