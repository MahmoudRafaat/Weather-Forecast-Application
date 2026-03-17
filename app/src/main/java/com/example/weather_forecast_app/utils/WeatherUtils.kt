package com.example.weather_forecast_app.utils

import com.example.weather_forecast_app.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object WeatherUtils {
    fun formatUnixTimestamp(timestamp: Int, pattern: String): String {
        val date = Date(timestamp.toLong() * 1000)
        val locale = Locale.getDefault()
        val sdf = SimpleDateFormat(pattern, locale)
        sdf.numberFormat = NumberFormat.getInstance(locale)
        return sdf.format(date)
    }

    fun formatTime(timestamp: Long, pattern: String): String {
        val date = Date(timestamp)
        val locale = Locale.getDefault()
        val sdf = SimpleDateFormat(pattern, locale)
        sdf.numberFormat = NumberFormat.getInstance(locale)
        return sdf.format(date)
    }

    fun formatNumber(number: Number): String {
        return NumberFormat.getInstance(Locale.getDefault()).format(number)
    }

    fun convertWindSpeed(speed: Double, toUnit: String, fromSystem: String): Double {
        // OpenWeatherMap 'wind.speed' units:
        // metric: meter/sec
        // imperial: miles/hour
        // standard: meter/sec
        
        // Convert input speed to m/s first
        val speedInMs = if (fromSystem == "imperial") speed / 2.23694 else speed
        
        return when (toUnit) {
            "m/s" -> speedInMs
            "mph" -> speedInMs * 2.23694
            else -> speedInMs
        }
    }

    fun getWeatherIcon(iconCode: String, isBig: Boolean): Int {
        val isNight = iconCode.endsWith("n")
        return when {
            iconCode.startsWith("01") -> { // Clear sky
                if (isNight) {
                    if (isBig) R.drawable.big_moon_cloud_fast_wind else R.drawable.small_moon_cloud_fast_wind
                } else {
                    if (isBig) R.drawable.big_sun_cloud_mid_rain else R.drawable.small_sun_cloud_mid_rain
                }
            }
            iconCode.startsWith("02") || iconCode.startsWith("03") || iconCode.startsWith("04") -> { // Clouds
                if (isNight) {
                    if (isBig) R.drawable.big_moon_cloud_fast_wind else R.drawable.small_moon_cloud_fast_wind
                } else {
                    if (isBig) R.drawable.big_sun_cloud_mid_rain else R.drawable.small_sun_cloud_mid_rain
                }
            }
            iconCode.startsWith("09") || iconCode.startsWith("10") -> { // Rain
                if (isNight) {
                    if (isBig) R.drawable.big_moon_cloud_mid_rain else R.drawable.small_moon_cloud_mid_rain
                } else {
                    if (isBig) R.drawable.big_sun_cloud_angled_rain else R.drawable.small_sun_cloud_angled_rain
                }
            }
            iconCode.startsWith("11") -> { // Thunderstorm
                if (isBig) R.drawable.big_tornado else R.drawable.small_tornado
            }
            iconCode.startsWith("13") -> { // Snow
                if (isNight) {
                    if (isBig) R.drawable.big_moon_cloud_fast_wind else R.drawable.small_moon_cloud_fast_wind
                } else {
                    if (isBig) R.drawable.big_sun_cloud_mid_rain else R.drawable.small_sun_cloud_mid_rain
                }
            }
            iconCode.startsWith("50") -> { // Mist/Fog
                if (isNight) {
                    if (isBig) R.drawable.big_moon_cloud_fast_wind else R.drawable.small_moon_cloud_fast_wind
                } else {
                    if (isBig) R.drawable.big_tornado else R.drawable.small_tornado
                }
            }
            else -> if (isBig) R.drawable.big_sun_cloud_mid_rain else R.drawable.small_sun_cloud_mid_rain
        }
    }
}
