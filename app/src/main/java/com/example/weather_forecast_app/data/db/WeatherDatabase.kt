package com.example.weather_forecast_app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather_forecast_app.data.weather.model.ForecastEntity
import com.example.weather_forecast_app.data.weather.datasource.local.WeatherDao
import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import com.example.weather_forecast_app.data.weather.model.WeatherTypeConverters

@Database(entities = [WeatherEntity::class, ForecastEntity::class, FavoriteLocation::class, WeatherAlert::class], version = 4, exportSchema = false)
@TypeConverters(WeatherTypeConverters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_db"
                )
                    .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
