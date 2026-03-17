package com.example.weather_forecast_app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.db.WeatherDatabase
import com.example.weather_forecast_app.data.network.RetrofitHelper
import com.example.weather_forecast_app.data.weather.datasource.local.WeatherLocalDataSourceImpl
import com.example.weather_forecast_app.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.example.weather_forecast_app.data.weather.repository.WeatherRepositoryImpl
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class AlertWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val alertId = inputData.getInt("ALERT_ID", -1)
        if (alertId == -1) return Result.failure()

        val database = WeatherDatabase.getInstance(context)
        val dao = database.weatherDao()
        val alert = dao.getAlertById(alertId) ?: return Result.failure()

        val localDataSource = WeatherLocalDataSourceImpl(dao, context)
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.api)
        val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
        val lang = repository.getLanguage().first()
        val localizedContext = getLocalizedContext(context, lang)

        val units = repository.getUnits().first()
        val manual = repository.getManualLocation().first()
        val lat = manual?.first ?: 0.0
        val lon = manual?.second ?: 0.0

        var weatherDescription = ""
        var temp = ""
        var lastUpdateMsg = ""
        var mainWeather = ""
        val unitSymbol = if (units == "metric") localizedContext.getString(R.string.unit_celsius) else "°F"

        try {
            val weatherResult = repository.fetchWeatherForLocation(lat, lon, units, lang)
            if (weatherResult != null) {
                weatherDescription = weatherResult.weather[0].description
                temp = weatherResult.main.temp.toInt().toString()
                mainWeather = weatherResult.weather[0].main
            } else {
                throw Exception("Failed to fetch")
            }
        } catch (e: Exception) {
            val localWeather = repository.getCurrentWeatherLocal().first()
            weatherDescription = localWeather?.description ?: ""
            temp = localWeather?.temp?.toInt()?.toString() ?: "--"
            // Since we don't store main weather in lean entity anymore, we use description or a mapping
            mainWeather = localWeather?.description ?: "" 
            
            val sdf = SimpleDateFormat("HH:mm", Locale(lang))
            lastUpdateMsg = "\n" + localizedContext.getString(R.string.last_update_format, sdf.format(Date()))
        }

        val advice = when {
            mainWeather.contains("snow", ignoreCase = true) -> localizedContext.getString(R.string.weather_warning_snow)
            mainWeather.contains("rain", ignoreCase = true) || 
            mainWeather.contains("drizzle", ignoreCase = true) || 
            mainWeather.contains("thunderstorm", ignoreCase = true) -> localizedContext.getString(R.string.weather_warning_rain)
            mainWeather.contains("cloud", ignoreCase = true) -> localizedContext.getString(R.string.weather_warning_clouds)
            else -> localizedContext.getString(R.string.weather_good_advice)
        }

        val finalMessage = if (weatherDescription.isNotEmpty()) {
            val base = localizedContext.getString(
                R.string.weather_report_format,
                weatherDescription.replaceFirstChar { it.uppercase() },
                temp,
                unitSymbol,
                advice
            )
            if (lastUpdateMsg.isNotEmpty()) "$base$lastUpdateMsg" else base
        } else {
            localizedContext.getString(R.string.weather_is_good)
        }

        handleAlertTrigger(alert.type, finalMessage, alertId, lang)
        dao.deleteAlert(alert)

        return Result.success()
    }

    private fun getLocalizedContext(baseContext: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)
        return baseContext.createConfigurationContext(config)
    }

    private fun handleAlertTrigger(type: String, message: String, alertId: Int, lang: String) {
        if (type == "alarm") {
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra("ALERT_ID", alertId)
                putExtra("MESSAGE", message)
                putExtra("LANG", lang)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } else {
            showStandardNotification(message, alertId, lang)
        }
    }

    private fun showStandardNotification(message: String, alertId: Int, lang: String) {
        val localizedContext = getLocalizedContext(context, lang)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "weather_notification_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = localizedContext.getString(R.string.notification)
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(localizedContext.getString(R.string.weather_alert))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setColor(0xFF48319D.toInt())
            .setColorized(true)

        notificationManager.notify(alertId, builder.build())
    }
}
