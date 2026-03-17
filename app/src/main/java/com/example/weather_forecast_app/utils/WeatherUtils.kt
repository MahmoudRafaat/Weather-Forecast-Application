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

        
        val speedInMs = if (fromSystem == "imperial") speed / 2.23694 else speed
        
        return when (toUnit) {
            "m/s" -> speedInMs
            "mph" -> speedInMs * 2.23694
            else -> speedInMs
        }
    }fun getWeatherIcon(iconCode: String, isBig: Boolean): Int {
        val isNight = iconCode.endsWith("n")

        return when (iconCode.substring(0, 2)) {
            "01" -> {
                if (isNight) (if (isBig) R.drawable.big_moon else R.drawable.small_moon)
                else (if (isBig) R.drawable.big_sun else R.drawable.small_sun)
            }
            "02", "03" -> {
                if (isNight) (if (isBig) R.drawable.big_moon_cloud_fast_wind else R.drawable.small_moon_cloud_fast_wind)
                else (if (isBig) R.drawable.big_sun_cloud_mid_rain else R.drawable.small_sun_cloud_mid_rain)
            }
            "04" -> {
                if (isBig) R.drawable.big_cloud else R.drawable.small_cloud
            }
            "09", "10" -> {
                if (isBig) R.drawable.big_sun_cloud_angled_rain else R.drawable.small_sun_cloud_angled_rain
            }
            "11" -> {
                if (isBig) R.drawable.big_tornado else R.drawable.small_tornado
            }
            "13" -> {
                if (isBig) R.drawable.big_snow else R.drawable.small_snow
            }
            "50" -> {
                if (isBig) R.drawable.big_fog else R.drawable.small_fog
            }
            else -> if (isBig) R.drawable.big_sun_cloud_mid_rain else R.drawable.small_sun_cloud_mid_rain
        }
    }

}
