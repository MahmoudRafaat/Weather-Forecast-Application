package com.example.weather_forecast_app.presentation.favorites.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.remote.creation.first
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.repository.WeatherRepository
import com.example.weather_forecast_app.utils.ConnectivityObserver
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var repository: WeatherRepository
    private lateinit var connectivityObserver: ConnectivityObserver
    private val testDispatcher = UnconfinedTestDispatcher()
    private val favorites = listOf(FavoriteLocation(id = 1, lat = 30.0, lon = 31.0, cityName = "aswan", countryName = "egypt"))


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk( relaxed = true)
        connectivityObserver = mockk()
        every { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Available)
        // Given list of favorites
        every { repository.getAllFavorites() } returns flowOf(favorites)

        viewModel= FavoriteViewModel(repository, connectivityObserver)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_onInitialization_emitsSuccessWithCorrectData() = runTest {
        // When view model is initialized
        // Then ui state is success with correct data
        val state = viewModel.uiState.first()
        val successState = state as FavoriteUiState.Success
        assertThat(state, `is`(FavoriteUiState.Success(favorites, true)))
        assertThat(successState.favorites, `is`(favorites))
    }

    @Test
    fun onAddFavoriteClicked_isOnline_emitsToNavigateToMap() = runTest {
        //Given collecting navigation events
        val events = mutableListOf<Unit>()

        val job = launch (testDispatcher){
            viewModel.navigateToMap.collect {
                events.add(it)
            }
        }
        //when add favorite is clicked
        viewModel.onAddFavoriteClicked()

        advanceUntilIdle()
        //then navigation is triggered
        assertThat(events.size, `is`(1))

        job.cancel()
    }
//
    @Test
    fun onAddFavoriteClicked_isOffline_doesNotEmitNavigation() = runTest {
        // Given offline mode in view model
    val offlineObserver = mockk<ConnectivityObserver>()
    every { offlineObserver.observe() } returns flowOf(ConnectivityObserver.Status.Unavailable)
        val offlineViewModel = FavoriteViewModel(repository, offlineObserver)
     val stateJob = launch {
        offlineViewModel.uiState.collect()
    }
        val navEvents = mutableListOf<Unit>()
        val job = launch { offlineViewModel.navigateToMap.toList(navEvents) }

        // When add favorite is clicked
    offlineViewModel.onAddFavoriteClicked()

        // Then navigation is not triggered
        assertThat(navEvents.size, `is`(0))
        stateJob.cancel()
        job.cancel()
    }

    @Test
    fun deleteFavorite_location_callsRepositoryDelete() = runTest {
    val stateJob = launch {
        viewModel.uiState.collect()
    }
        // When delete is requested
        viewModel.deleteFavorite(favorites[0])

        // Then delete is called
        coVerify { repository.deleteFavorite(favorites[0]) }
        stateJob.cancel()
    }

    @Test
    fun onDeleteRequest_location_emitsShowDeleteConfirmation() = runTest {
        // Given collecting events
        val stateJob = launch {
            viewModel.uiState.collect()
        }
        val events = mutableListOf<FavoriteLocation>()
        val job = launch { viewModel.showDeleteConfirmation.toList(events) }
        advanceUntilIdle()
        // When delete is requested
        viewModel.onDeleteRequest(favorites[0])
        advanceUntilIdle()


        // Then event is triggered
        assertThat(events.size, `is`(1))
        assertThat(events[0], `is`(favorites[0]))
        stateJob.cancel()
        job.cancel()
    }


}

