package com.example.weather_forecast_app.services

import android.content.Context
import androidx.work.*
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import java.util.concurrent.TimeUnit

class WeatherAlertScheduler(private val context: Context) {

    fun schedule(alert: WeatherAlert) {
        val currentTime = System.currentTimeMillis()
        val delay = alert.startTime - currentTime

        if (delay > 0) {
            val inputData = Data.Builder()
                .putInt("ALERT_ID", alert.id)
                .putString("ALERT_TYPE", alert.type)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<AlertWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("alert_${alert.id}")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "weather_alert_${alert.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    fun cancel(alertId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("weather_alert_$alertId")
    }
}
