package com.example.weather_forecast_app.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val alertId = intent.getIntExtra("ALERT_ID", -1)

        if (action == "ACTION_DISMISS_ALARM") {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(alertId)
            val stopIntent = Intent(context, AlarmService::class.java).apply {
                this.action = "STOP_ALARM"
            }
            context.stopService(stopIntent)
            return
        }

        val alertType = intent.getStringExtra("ALERT_TYPE") ?: "notification"

        if (alertId != -1) {
            val inputData = Data.Builder()
                .putInt("ALERT_ID", alertId)
                .putString("ALERT_TYPE", alertType)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<AlertWorker>()
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "weather_alert_$alertId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
