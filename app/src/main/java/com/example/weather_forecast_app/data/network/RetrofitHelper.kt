package com.example.weather_forecast_app.data.network

import com.example.weather_forecast_app.BuildConfig
import com.example.weather_forecast_app.data.weather.datasource.remote.WeatherService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private val BASE_URL = BuildConfig.BASE_URL
    
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()
        
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val api: WeatherService = retrofit.create(WeatherService::class.java)
}
