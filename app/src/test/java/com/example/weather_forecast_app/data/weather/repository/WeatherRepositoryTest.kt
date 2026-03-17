package com.example.weather_forecast_app.data.weather.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_forecast_app.data.weather.datasource.local.WeatherLocalDataSource
import com.example.weather_forecast_app.data.weather.datasource.remote.WeatherRemoteDataSource

import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import com.example.weather_forecast_app.data.weather.model.WeatherModel
import com.example.weather_forecast_app.data.weather.repository.fakemodel.TestDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.hamcrest.core.Is.`is`
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class WeatherRepositoryTest {
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var remoteDataSource: WeatherRemoteDataSource
    @Before
    fun setUp() {
        localDataSource=mockk(relaxed = true)
        remoteDataSource=mockk()
        weatherRepository=WeatherRepositoryImpl(remoteDataSource,localDataSource)

    }



    @Test
    fun fetchWeatherForLocation_latLonUnitsLang_getsWeather() = runTest {

        val fakeRemoteModel = TestDataFactory.fakeWeatherModel()
        coEvery {
            remoteDataSource.getCurrentWeather(any(), any(), any(), any())
        } returns Response.success(fakeRemoteModel)
        //When fetch weather for location
        val weather = weatherRepository.fetchWeatherForLocation(1.0, 2.0, "metric", "en")
        //Then Verify the remote data source was called with specific parameters the result isn't null and matches the expected city name
        coVerify(exactly = 1) {
            remoteDataSource.getCurrentWeather(1.0, 2.0, "metric", "en")
        }
        assertThat(weather, `is`(notNullValue()))
        assertThat(weather?.name, `is`(fakeRemoteModel.name))
    }

    @Test
    fun getCurrentWeatherLocal_latestSavedWeather()= runTest {
        //Given a mocking weather entity
        val mockWeatherBody = mockk<WeatherEntity>(relaxed = true)
        coEvery { localDataSource.getCurrentWeather() } returns flowOf(mockWeatherBody)
        //When get current weather locally
        val weather=weatherRepository.getCurrentWeatherLocal().first()
        //Then return the latest saved weather
        assertThat(weather, Is.`is`(mockWeatherBody))
    }

    @Test
    fun refreshWeather_locationLatLonUnitsLang_getsWeatherAndSavedInLocal() = runTest {
        //Given a mocking weather entity
        coEvery {
            remoteDataSource.getCurrentWeather(any(), any(), any(), any())
        } returns Response.success(TestDataFactory.fakeWeatherModel())

        coEvery {
            remoteDataSource.getHourlyForecast(any(), any(), any(), any())
        } returns Response.success(TestDataFactory.fakeForecast())

        //When refresh weather
        weatherRepository.refreshWeather(1.0, 2.0, "metric", "en")
        //Then weather and forecast are saved in local
        coVerify { localDataSource.insertCurrentWeather(any()) }
        coVerify{ localDataSource.insertForecast(any()) }
    }
    @Test
    fun refreshWeather_locationLatLonUnitsLang_weatherFails() = runTest {
        // Given a failure response from remote data source
        val errorCode = 500
        coEvery {
            remoteDataSource.getCurrentWeather(any(), any(), any(), any())
        } returns Response.error(errorCode, mockk(relaxed = true))

        coEvery {
            remoteDataSource.getHourlyForecast(any(), any(), any(), any())
        } returns Response.success(TestDataFactory.fakeForecast())

        // When refresh weather is called
        val result = runCatching {
            weatherRepository.refreshWeather(1.0, 2.0, "metric", "en")
        }

        // Then weather is not saved in local and throws exception
        coVerify(exactly = 0) { localDataSource.insertCurrentWeather(any()) }
        assertThat(result.isFailure, `is`(true))
        assertThat(result.exceptionOrNull()?.message, `is`(errorCode.toString()))
    }


    @Test
    fun insertAlert_alert_alertIsSavedInLocal() =runTest {
        //Given a mocking weather alert
        val mockAlert = mockk<WeatherAlert>(relaxed = true)
        //When insert alert
        weatherRepository.insertAlert(mockAlert)
        //Then alert is saved in local
        coVerify { localDataSource.insertAlert(any())
        }

    }

    @Test
    fun deleteAlert_alert_alertIsRemovedFromLocal() =runTest  {
        //Given a mocking weather alert
        val mockAlert = mockk<WeatherAlert>(relaxed = true)
        //When delete alert
        weatherRepository.deleteAlert(mockAlert)
        //Then alert is removed from local
        coVerify { localDataSource.deleteAlert(any())
        }

    }






    }





