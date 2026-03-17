package com.example.weather_forecast_app.presentation.favorites.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.db.WeatherDatabase
import com.example.weather_forecast_app.data.network.RetrofitHelper
import com.example.weather_forecast_app.data.weather.datasource.local.WeatherLocalDataSourceImpl
import com.example.weather_forecast_app.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.data.weather.repository.WeatherRepository
import com.example.weather_forecast_app.data.weather.repository.WeatherRepositoryImpl
import com.example.weather_forecast_app.utils.ConnectivityObserver
import com.example.weather_forecast_app.utils.NetworkConnectivityObserver
import com.example.weather_forecast_app.utils.UiText
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: WeatherRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val connectivityStatus = connectivityObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityObserver.Status.Unavailable)

    val uiState: StateFlow<FavoriteUiState> = combine(
        repository.getAllFavorites(),
        connectivityStatus
    ) { favorites, status ->
        val successState: FavoriteUiState = FavoriteUiState.Success(
            favorites = favorites,
            isOnline = status == ConnectivityObserver.Status.Available
        )
        successState
    }.catch {
        emit(FavoriteUiState.Error(UiText.StringResource(R.string.unknown_error))) 
    }.stateIn(viewModelScope, SharingStarted.Eagerly, FavoriteUiState.Loading)

    private val _showDeleteConfirmation = MutableSharedFlow<FavoriteLocation>()
    val showDeleteConfirmation = _showDeleteConfirmation.asSharedFlow()

    private val _navigateToMap = MutableSharedFlow<Unit>()
    val navigateToMap = _navigateToMap.asSharedFlow()

    private val _navigateToDetails = MutableSharedFlow<FavoriteLocation>()
    val navigateToDetails = _navigateToDetails.asSharedFlow()

    fun onAddFavoriteClicked() {
        if (connectivityStatus.value == ConnectivityObserver.Status.Available) {
            viewModelScope.launch {
                _navigateToMap.emit(Unit)
            }
        }
    }

    fun onFavoriteClicked(location: FavoriteLocation) {
        viewModelScope.launch {
            _navigateToDetails.emit(location)
        }
    }

    fun onDeleteRequest(location: FavoriteLocation) {
        viewModelScope.launch {
            _showDeleteConfirmation.emit(location)
        }
    }

    fun deleteFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            try {
                repository.deleteFavorite(location)
            } catch (e: Exception) {
                // Background operation
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FavoriteViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val database = WeatherDatabase.getInstance(app)
            val localDataSource = WeatherLocalDataSourceImpl(database.weatherDao(), app)
            val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.api)
            val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
            val connectivityObserver = NetworkConnectivityObserver(app)
            return FavoriteViewModel(repository, connectivityObserver) as T
        }
}
