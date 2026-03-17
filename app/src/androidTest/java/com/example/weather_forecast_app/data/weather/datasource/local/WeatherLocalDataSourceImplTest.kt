package com.example.weather_forecast_app.data.weather.datasource.local

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weather_forecast_app.data.db.WeatherDatabase
import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceImplTest {
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var weatherDao: WeatherDao
    private lateinit var database: WeatherDatabase
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @Before
    fun setUp(){
        val application = ApplicationProvider.getApplicationContext<Application>()
        database= Room.inMemoryDatabaseBuilder(
            application,
            WeatherDatabase::class.java).allowMainThreadQueries().build()
        weatherDao=database.weatherDao()
        localDataSource=WeatherLocalDataSourceImpl(weatherDao,application)
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
        // When inserting the weather entity into the local data source
        localDataSource.insertCurrentWeather(weatherEntity)
        // Then the retrieved weather entity matches the inserted one
        val retrievedWeather = localDataSource.getCurrentWeather().first()
        assertThat(retrievedWeather, `is`(weatherEntity))

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
        // When inserting the favorite location into the local data source
        localDataSource.insertFavorite(favoriteLocation)

        // Then the retrieved favorite locations list contains the inserted one
        val favorites = localDataSource.getAllFavorites().first()
        assertThat(favorites, `is`(listOf(favoriteLocation)))
    }

    @Test
    fun deleteFavoriteLocation_getsEmptyFavorites() = runTest {
        // Given a favorite location inserted in local data source
        val favoriteLocation = FavoriteLocation(
            id = 1,
            lat = 30.0444,
            lon = 31.2357,
            cityName = "Aswan",
            countryName = "Egypt"
        )
        localDataSource.insertFavorite(favoriteLocation)
        // When deleting the favorite location
        localDataSource.deleteFavorite(favoriteLocation)
        // Then the favorites list is empty
        val favorites = localDataSource.getAllFavorites().first()
        assertThat(favorites, `is`(emptyList()))
    }

    @Test
    fun insertAlert_getsAlerts() = runTest{
        // Given a weather alert
        val alert = WeatherAlert(
            id = 1,
            type = "alarm",
            startTime = 1623456789,
            endTime = 16234567,
            isEnabled = true
        )
        // When inserting the alert into the local data source
        localDataSource.insertAlert(alert)

        // Then the retrieved alerts list contains the inserted one
        val alerts = localDataSource.getAllAlerts().first()
        assertThat(alerts, `is`(listOf(alert)))

    }

    @Test
    fun deleteAlert_getsEmptyAlerts() = runTest {
        // Given a weather alert inserted in local data
        val alert = WeatherAlert(
            id = 1,
            type = "alarm",
            startTime = 1623456789,
            endTime = 16234567,
            isEnabled = true
        )
        localDataSource.insertAlert(alert)
        // When deleting the alert
        localDataSource.deleteAlert(alert)
        // Then the alerts list is empty
        val alerts = localDataSource.getAllAlerts().first()
        assertThat(alerts, `is`(emptyList()))
    }

}