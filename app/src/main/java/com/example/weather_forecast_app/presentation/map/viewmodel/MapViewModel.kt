package com.example.weather_forecast_app.presentation.map.viewmodel

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.util.Locale

class MapViewModel(
    private val repository: WeatherRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _selectedPoint = MutableStateFlow<GeoPoint?>(null)
    val selectedPoint: StateFlow<GeoPoint?> = _selectedPoint.asStateFlow()

    private val _cityName = MutableStateFlow<String?>(null)
    val cityName: StateFlow<String?> = _cityName.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    val isOnline = connectivityObserver.observe()
        .map { it == ConnectivityObserver.Status.Available }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun onPointSelected(context: Context, point: GeoPoint) {
        _selectedPoint.value = point
        _cityName.value = null
        
        viewModelScope.launch {
            val lang = try { repository.getLanguage().first() } catch (e: Exception) { "en" }
            fetchCityName(context, point.latitude, point.longitude, lang)
        }
    }

    private suspend fun fetchCityName(context: Context, lat: Double, lon: Double, langCode: String) {
        val name = withContext(Dispatchers.IO) {
            try {
                val locale = if (langCode == "ar") Locale("ar") else Locale.ENGLISH
                val geocoder = Geocoder(context, locale)
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val governorate = address.adminArea
                    val country = address.countryName
                    
                    val parts = linkedSetOf<String>()
                    if (!governorate.isNullOrBlank()) parts.add(governorate)
                    if (!country.isNullOrBlank()) parts.add(country)
                    
                    if (parts.isNotEmpty()) {
                        parts.joinToString(", ")
                    } else {
                        context.getString(R.string.unknown_location)
                    }
                } else {
                    context.getString(R.string.unknown_location)
                }
            } catch (e: Exception) {
                context.getString(R.string.unknown_location)
            }
        }
        _cityName.value = name
    }

    fun confirmLocation(isFavoriteMode: Boolean) {
        viewModelScope.launch {
            try {
                val point = _selectedPoint.value
                val name = _cityName.value
                if (point != null) {
                    if (isFavoriteMode) {
                        repository.insertFavorite(
                            FavoriteLocation(
                                lat = point.latitude,
                                lon = point.longitude,
                                cityName = name ?: "Unknown City"
                            )
                        )
                    } else {
                        repository.setManualLocation(point.latitude, point.longitude, name)
                        repository.setLocationMode("map")
                    }
                    _navigateBack.emit(Unit)
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to save location")
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigateBack.emit(Unit)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MapViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val database = WeatherDatabase.getInstance(app)
            val localDataSource = WeatherLocalDataSourceImpl(database.weatherDao(), app)
            val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.api)
            val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
            val connectivityObserver = NetworkConnectivityObserver(app)
            return MapViewModel(repository, connectivityObserver) as T
        }
}
