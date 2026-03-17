package com.example.weather_forecast_app.data.network

import com.example.weather_forecast_app.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val updatedUrl: HttpUrl = originalRequest.url().newBuilder()
            .addQueryParameter("appid", BuildConfig.API_KEY)
            .build()
        val newRequest: Request = originalRequest.newBuilder()
            .url(updatedUrl)
            .build()
        return chain.proceed(newRequest)
    }
}
