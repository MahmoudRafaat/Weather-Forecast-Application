//package com.example.weather_forecast_app.presentation.favorites.viewmodel
//
//import android.app.Application
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
//import com.example.weather_forecast_app.data.weather.repository.WeatherRepository
//import com.example.weather_forecast_app.utils.ConnectivityObserver
//import io.mockk.every
//import io.mockk.mockk
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.flow.toList
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.test.UnconfinedTestDispatcher
//import kotlinx.coroutines.test.resetMain
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.After
//import org.junit.Before
//import org.junit.runner.RunWith
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.core.Is.`is`
//import org.junit.Rule
//import org.junit.Test
//
//@OptIn(ExperimentalCoroutinesApi::class)
//@RunWith(AndroidJUnit4::class)
//class FavoriteViewModelTest {
//    private lateinit var viewModel: FavoriteViewModel
//    private lateinit var repository: WeatherRepository
//    private lateinit var connectivityObserver: ConnectivityObserver
//    private lateinit var application: Application
//    private val testDispatcher = UnconfinedTestDispatcher()
//    private val favorites = listOf(FavoriteLocation(id = 1, lat = 30.0, lon = 31.0, cityName = "aswan", countryName = "egypt"))
//
//
//    @get:Rule
//    var instantExecutorRule = InstantTaskExecutorRule()
//
//    @Before
//    fun setUp() {
//        Dispatchers.setMain(testDispatcher)
//        application = ApplicationProvider.getApplicationContext<Application>()
//        repository = mockk()
//        connectivityObserver = mockk()
//        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Available)
//        // Given list of favorites
//        every { repository.getAllFavorites() } returns flowOf(favorites)
//
//        viewModel= FavoriteViewModel(repository, connectivityObserver)
//
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//    }
//
//    @Test
//    fun uiState_onInitialization_emitsSuccessWithCorrectData() = runTest {
//        // When view model is initialized
//        val collectJob = launch(UnconfinedTestDispatcher()) {
//            viewModel.uiState.collect()
//        }
//        // Then ui state is success with correct data
//        val state = viewModel.uiState.value
//        val successState = state as FavoriteUiState.Success
//        assertThat(state, `is`(FavoriteUiState.Success(favorites, true)))
//        assertThat(successState.favorites, `is`(favorites))
//         collectJob.cancel()
//
//    }
//
//    @Test
//    fun onAddFavoriteClicked_isOnline_emitsToNavigateToMap() = runTest {
//        // Given online mode in view model
//        val stateJob = launch(UnconfinedTestDispatcher()) {
//            viewModel.uiState.collect()
//        }
//        val navEvents = mutableListOf<Unit>()
//        val job = launch(UnconfinedTestDispatcher()) {
//            viewModel.navigateToMap.toList(navEvents)
//        }
//        // When add favorite is clicked
//        viewModel.onAddFavoriteClicked()
//
//        // Then navigation is triggered
//        assertThat(navEvents.size, `is`(1))
//        stateJob.cancel()
//        job.cancel()
//    }
////
//    @Test
//    fun onAddFavoriteClicked_isOffline_doesNotEmitNavigation() = runTest {
//        // Given offline mode in view model
//        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Unavailable)
//        val offlineViewModel = FavoriteViewModel(repository, connectivityObserver)
//     val stateJob = launch {
//        offlineViewModel.uiState.collect()
//    }
//        val navEvents = mutableListOf<Unit>()
//        val job = launch { viewModel.navigateToMap.toList(navEvents) }
//
//        // When add favorite is clicked
//        viewModel.onAddFavoriteClicked()
//
//        // Then navigation is not triggered
//        assertThat(navEvents.size, `is`(0))
//        stateJob.cancel()
//        job.cancel()
//    }
////
//    @Test
//    fun deleteFavorite_location_callsRepositoryDelete() = runTest {
//    val stateJob = launch(testDispatcher) {
//        viewModel.uiState.collect()
//    }
//        // When
//        viewModel.deleteFavorite(favorites[0])
//
//        // Then
//        assertThat(favorites.size, `is`(1))
//        stateJob.cancel()
//    }
//
//    @Test
//    fun onDeleteRequest_location_emitsShowDeleteConfirmation() = runTest {
//        // Given
//        val stateJob = launch(testDispatcher) {
//            viewModel.uiState.collect()
//        }
//        val events = mutableListOf<FavoriteLocation>()
//        val job = launch(testDispatcher) { viewModel.showDeleteConfirmation.toList(events) }
//
//        // When
//        viewModel.onDeleteRequest(favorites[0])
//
//        // Then
//        assertThat(events.size, `is`(1))
//        assertThat(events[0], `is`(favorites[0]))
//        stateJob.cancel()
//        job.cancel()
//    }
//
//
//}

package com.example.weather_forecast_app.presentation.favorites.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.repository.WeatherRepository
import com.example.weather_forecast_app.utils.ConnectivityObserver
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var repository: WeatherRepository
    private lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var application: Application

    private val testDispatcher = StandardTestDispatcher()

    private val favorites = listOf(
        FavoriteLocation(
            id = 1,
            lat = 30.0,
            lon = 31.0,
            cityName = "aswan",
            countryName = "egypt"
        )
    )

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        application = ApplicationProvider.getApplicationContext()
        repository = mockk(relaxed = true)
        connectivityObserver = mockk()

        every { connectivityObserver.observe() } returns
                flowOf(ConnectivityObserver.Status.Available)

        every { repository.getAllFavorites() } returns flowOf(favorites)

        viewModel = FavoriteViewModel(repository, connectivityObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun uiState_onInitialization_emitsSuccessWithCorrectData() = runTest {
        // When
        val state = viewModel.uiState.first()

        // Then
        assertThat(state is FavoriteUiState.Success, `is`(true))

        val success = state as FavoriteUiState.Success
        assertThat(success.favorites, `is`(favorites))
        assertThat(success.isOnline, `is`(true))
    }

    @Test
    fun onAddFavoriteClicked_isOnline_emitsToNavigateToMap() = runTest {
        val navEvents = mutableListOf<Unit>()

        val job = launch {
            viewModel.navigateToMap.toList(navEvents)
        }
        advanceUntilIdle()

        // When
        viewModel.onAddFavoriteClicked()
        advanceUntilIdle()

        // Then
        assertThat(navEvents.size, `is`(1))

        job.cancel()
    }

    @Test
    fun onAddFavoriteClicked_isOffline_doesNotEmitNavigation() = runTest {
        // Given
        every { connectivityObserver.observe() } returns
                flowOf(ConnectivityObserver.Status.Unavailable)

        val offlineViewModel = FavoriteViewModel(repository, connectivityObserver)

        val navEvents = mutableListOf<Unit>()
        val job = launch {
            offlineViewModel.navigateToMap.toList(navEvents)
        }

        // When
        offlineViewModel.onAddFavoriteClicked()
        advanceUntilIdle()

        // Then
        assertThat(navEvents.size, `is`(0))

        job.cancel()
    }

    @Test
    fun deleteFavorite_location_callsRepositoryDelete() = runTest {
        // Given
        coEvery { repository.deleteFavorite(any()) } just Runs

        // When
        viewModel.deleteFavorite(favorites[0])
        advanceUntilIdle()

        // Then
        coVerify { repository.deleteFavorite(favorites[0]) }
    }

    @Test
    fun onDeleteRequest_location_emitsShowDeleteConfirmation() = runTest {
        val events = mutableListOf<FavoriteLocation>()

        val job = launch {
            viewModel.showDeleteConfirmation.toList(events)
        }

        // When
        viewModel.onDeleteRequest(favorites[0])
        advanceUntilIdle()

        // Then
        assertThat(events.size, `is`(1))
        assertThat(events[0], `is`(favorites[0]))

        job.cancel()
    }
}