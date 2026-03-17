package com.example.weather_forecast_app.presentation.map.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_forecast_app.data.weather.repository.WeatherRepository
import com.example.weather_forecast_app.utils.ConnectivityObserver
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertNull
import org.junit.runner.RunWith
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MapViewModelTest {
    private lateinit var viewModel: MapViewModel
    private lateinit var repository: WeatherRepository
    private lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var application: Application
    private val testDispatcher = UnconfinedTestDispatcher()


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
         application = ApplicationProvider.getApplicationContext<Application>()
        repository = mockk()
        connectivityObserver = mockk()
        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Available)

        viewModel = MapViewModel(repository, connectivityObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()

    }

    @Test
    fun onPointSelected_goePoint_updatesSelectedPoint()= runTest{
        //Given point
        val point = GeoPoint(30.0444, 31.2357)
        every { repository.getLanguage() }returns flowOf("en")
        //when call onPointSelected
        viewModel.onPointSelected(application,point)
        //then selectedPoint is updated and cityName is null
        assertEquals(point,viewModel.selectedPoint.value)
    }


    @Test
    fun confirmLocation_isFavoriteModeTrue_callsInsertFavorite() = runTest {
        // Given geo point
        val testPoint = GeoPoint(30.0, 31.0)
        every { repository.getLanguage() } returns flowOf("en")
        coEvery { repository.insertFavorite(any()) } just Runs


        viewModel.onPointSelected(application, testPoint)
        val results = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.navigateBack.toList(results)
        }
        //when confirm location
        viewModel.confirmLocation(isFavoriteMode = true)
        //then insertFavorite is called
        coVerify { repository.insertFavorite(any()) }

        assertThat(results.size, `is`(1))
        job.cancel()

    }

    @Test
    fun confirmLocation_isFavoriteModeFalse_updatesManualLocationAndMode() = runTest {
        // Given geo point
        val testPoint = GeoPoint(30.0, 31.0)
        every { repository.getLanguage() } returns flowOf("en")
        coEvery { repository.setManualLocation(any(),any(),any()) } just Runs
        coEvery { repository.setLocationMode(any()) } just Runs

        viewModel.onPointSelected(application, testPoint)
        val results = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.navigateBack.toList(results)
        }
        // When confirm location
        viewModel.confirmLocation(isFavoriteMode = false)

        // Then manual location and mode are updated
        coVerify {
            repository.setManualLocation(any(),any(), any())
            repository.setLocationMode("map")
        }
        assertThat(results.size, `is`(1))
        job.cancel()

    }



    @Test
    fun onBackClicked_triggers_emitsToNavigateBackSharedFlow() = runTest {
        val results = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.navigateBack.toList(results)
        }
        // When back button is clicked
        viewModel.onBackClicked()
        // then navigateBack is triggered
        assertThat(results.size, `is`(1))
        job.cancel()
    }
}