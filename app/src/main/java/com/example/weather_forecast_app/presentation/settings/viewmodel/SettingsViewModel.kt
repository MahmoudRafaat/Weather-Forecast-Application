package com.example.weather_forecast_app.presentation.settings.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast_app.data.db.WeatherDatabase
import com.example.weather_forecast_app.data.network.RetrofitHelper
import com.example.weather_forecast_app.data.weather.datasource.local.WeatherLocalDataSourceImpl
import com.example.weather_forecast_app.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.example.weather_forecast_app.data.weather.repository.WeatherRepository
import com.example.weather_forecast_app.data.weather.repository.WeatherRepositoryImpl
import com.example.weather_forecast_app.utils.ConnectivityObserver
import com.example.weather_forecast_app.utils.NetworkConnectivityObserver
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: WeatherRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _navigateToMap = MutableSharedFlow<Unit>()
    val navigateToMap = _navigateToMap.asSharedFlow()

    private val connectivityStatus = connectivityObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityObserver.Status.Unavailable)

    val locationMode: StateFlow<String> = repository.getLocationMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")

    val units: StateFlow<String> = repository.getUnits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    val windSpeedUnit: StateFlow<String> = repository.getWindSpeedUnit()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "m/s")

    val language: StateFlow<String> = repository.getLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val themeMode: StateFlow<String> = repository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val isOnline: StateFlow<Boolean> = connectivityStatus
        .map { it == ConnectivityObserver.Status.Available }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun onNavigateToMap() {
        if (isOnline.value) {
            viewModelScope.launch {
                _navigateToMap.emit(Unit)
            }
        }
    }

    fun updateLocationMode(mode: String) {
        if (isOnline.value) {
            viewModelScope.launch {
                repository.setLocationMode(mode)
            }
        }
    }

    fun updateUnits(units: String) {
        if (isOnline.value) {
            viewModelScope.launch {
                repository.setUnits(units)
            }
        }
    }

    fun updateWindSpeedUnit(unit: String) {
        if (isOnline.value) {
            viewModelScope.launch {
                repository.setWindSpeedUnit(unit)
            }
        }
    }

    fun updateLanguage(language: String) {
        if (isOnline.value) {
            viewModelScope.launch {
                repository.setLanguage(language)
            }
        }
    }

    fun updateThemeMode(mode: String) {
        if (isOnline.value) {
            viewModelScope.launch {
                repository.setThemeMode(mode)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val database = WeatherDatabase.getInstance(app)
            val localDataSource = WeatherLocalDataSourceImpl(database.weatherDao(), app)
            val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.api)
            val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
            val connectivityObserver = NetworkConnectivityObserver(app)
            return SettingsViewModel(repository, connectivityObserver) as T
        }
}
