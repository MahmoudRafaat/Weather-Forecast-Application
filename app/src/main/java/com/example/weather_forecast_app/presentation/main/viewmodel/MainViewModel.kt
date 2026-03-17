package com.example.weather_forecast_app.presentation.main.viewmodel

import android.app.Application
import android.location.Geocoder
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(
    private val repository: WeatherRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val app: Application
) : ViewModel() {

    val themeMode: StateFlow<String> = repository.getThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "system"
        )

    val language: StateFlow<String> = repository.getLanguage()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )

    init {
        observeLanguageAndSyncFavorites()
    }

    private fun observeLanguageAndSyncFavorites() {
        combine(
            repository.getLanguage(),
            connectivityObserver.observe()
        ) { lang, status -> lang to status }
            .distinctUntilChanged()
            .onEach { (lang, status) ->
                if (status == ConnectivityObserver.Status.Available) {
                    syncFavoriteNamesToDatabase(lang)
                }
            }.launchIn(viewModelScope)
    }

    private fun syncFavoriteNamesToDatabase(lang: String) {
        viewModelScope.launch {
            val favorites = repository.getAllFavorites().first()
            if (favorites.isEmpty()) return@launch
            
            val geocoder = Geocoder(app, Locale(lang))
            favorites.forEach { loc ->
                launch(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(loc.lat, loc.lon, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            val governorate = address.adminArea
                            val country = address.countryName
                            val newName = governorate?: country?: loc.cityName
                            if (newName != loc.cityName) {
                                repository.insertFavorite(loc.copy(cityName = newName))
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = WeatherDatabase.getInstance(app)
        val localDataSource = WeatherLocalDataSourceImpl(database.weatherDao(), app)
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.api)
        val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
        val connectivityObserver = NetworkConnectivityObserver(app)
        return MainViewModel(repository, connectivityObserver, app) as T
    }
}
