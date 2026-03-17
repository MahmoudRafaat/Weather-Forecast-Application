package com.example.weather_forecast_app.data.weather.datasource.local

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_forecast_app.data.db.WeatherDatabase
import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {
    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @Before
    fun setUp(){
        val application = ApplicationProvider.getApplicationContext<Application>()
        database= Room.inMemoryDatabaseBuilder(
            application,
            WeatherDatabase::class.java).allowMainThreadQueries().build()
        weatherDao=database.weatherDao()
    }
    @After
    fun tearDown(){
        database.close()
    }
    @Test
    fun insertCurrentWeather_getsCurrentWeather() = runTest {
        // Given a weather entity
        val weatherEntity = WeatherEntity(
            id = 0,
            cityName = "aswan",
            temp = 20.0,
            description = "Sunny",
            icon = "01d",
            humidity = 60,
            windSpeed = 5.0,
            pressure = 1013,
            clouds = 0,
            dt = 1623456789
        )
        // When inserting the weather entity into the database
        weatherDao.insertCurrentWeather(weatherEntity)

        // Then the retrieved weather entity matches the inserted one
        val retrievedWeather = weatherDao.getCurrentWeather().first()
        assertThat(retrievedWeather, Is.`is`(weatherEntity))

    }
    @Test
    fun insertFavoriteLocation_getsFavoriteLocations() = runTest {
        // Given a favorite location
        val favoriteLocation = FavoriteLocation(
            id = 1,
            lat = 30.0444,
            lon = 31.2357,
            cityName = "Aswan",
            countryName = "Egypt"
        )
        // When inserting the favorite location into the database
        weatherDao.insertFavorite(favoriteLocation)
        // Then the retrieved favorite locations list contains the inserted one
        val favorites = weatherDao.getAllFavorites().first()
       assertThat(favorites, Is.`is`(listOf(favoriteLocation)))

    }

    @Test
    fun deleteFavoriteLocation_getsEmptyFavorites() = runTest {
        // Given a favorite location inserted in database
        val favoriteLocation = FavoriteLocation(
            id = 1,
            lat = 30.0444,
            lon = 31.2357,
            cityName = "Aswan",
            countryName = "Egypt"
        )
        weatherDao.insertFavorite(favoriteLocation)

        // When deleting the favorite location
        weatherDao.deleteFavorite(favoriteLocation)

        // Then the favorites list is empty
        val favorites = weatherDao.getAllFavorites().first()
        assertThat(favorites, Is.`is`(emptyList()))


    }
    @Test
    fun insertAlert_getsAlerts() = runTest {
        // Given a weather alert
        val alert = WeatherAlert(
            id = 1,
            type = "alarm",
            startTime = 1623456789,
            endTime = 16234567,
            isEnabled = true
        )
        // When inserting the alert into the database
        weatherDao.insertAlert(alert)

        // Then the retrieved alerts list contains the inserted one
        val alerts = weatherDao.getAllAlerts().first()
        assertThat(alerts, Is.`is`(listOf(alert)))
    }
    @Test
     fun deleteAlert_getsEmptyAlerts() = runTest {
        // Given a weather alert inserted in database
        val alert = WeatherAlert(
            id = 1,
            type = "alarm",
            startTime = 1623456789,
            endTime = 16234567,
            isEnabled = true
        )
        weatherDao.insertAlert(alert)
        // When deleting the alert
        weatherDao.deleteAlert(alert)

        // Then the alerts list is empty
        val alerts = weatherDao.getAllAlerts().first()
        assertThat(alerts, Is.`is`(emptyList()))
    }





    }