package com.example.weather_forecast_app.presentation.favoritedetails.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.db.WeatherDatabase
import com.example.weather_forecast_app.data.network.RetrofitHelper
import com.example.weather_forecast_app.data.weather.datasource.local.WeatherLocalDataSourceImpl
import com.example.weather_forecast_app.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.example.weather_forecast_app.data.weather.model.LocalForecastItem
import com.example.weather_forecast_app.data.weather.model.WeatherEntity
import com.example.weather_forecast_app.data.weather.repository.WeatherRepository
import com.example.weather_forecast_app.data.weather.repository.WeatherRepositoryImpl
import com.example.weather_forecast_app.presentation.home.viewmodel.HomeUiState
import com.example.weather_forecast_app.utils.ConnectivityObserver
import com.example.weather_forecast_app.utils.NetworkConnectivityObserver
import com.example.weather_forecast_app.utils.UiText
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FavoriteDetailsViewModel(
    private val repository: WeatherRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val lat: Double,
    private val lon: Double,
    private val savedCityName: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _noInternet = MutableStateFlow(false)
    val noInternet = _noInternet.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("")

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    private val connectivityStatus = connectivityObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityObserver.Status.Unavailable)

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        connectivityStatus.onEach { status ->
            if (status == ConnectivityObserver.Status.Available) {
                fetchWeatherData()
            } else if (_uiState.value is HomeUiState.Loading || _uiState.value is HomeUiState.Error) {
                _noInternet.value = true
            }
        }.launchIn(viewModelScope)
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigateBack.emit(Unit)
        }
    }

    fun retry() {
        fetchWeatherData()
    }

    private fun updateSyncTime() {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        _lastSyncTime.value = sdf.format(Date())
    }

    fun fetchWeatherData() {
        viewModelScope.launch {
            if (connectivityStatus.value != ConnectivityObserver.Status.Available) {
                _noInternet.value = true
                _isRefreshing.value = false
                return@launch
            }

            _uiState.value = HomeUiState.Loading
            _isRefreshing.value = true
            _noInternet.value = false
            
            try {
                val units = repository.getUnits().first()
                val windSpeedUnit = repository.getWindSpeedUnit().first()
                val lang = repository.getLanguage().first()
                
                val weather = repository.fetchWeatherForLocation(lat, lon, units, lang)
                val forecast = repository.fetchForecastForLocation(lat, lon, units, lang)
                
                if (weather != null && forecast != null) {
                    updateSyncTime()
                    
                    val weatherEntity = WeatherEntity(
                        cityName = savedCityName,
                        temp = weather.main.temp,
                        description = weather.weather[0].description,
                        icon = weather.weather[0].icon,
                        humidity = weather.main.humidity,
                        windSpeed = weather.wind.speed,
                        pressure = weather.main.pressure,
                        clouds = weather.clouds.all,
                        dt = weather.dt
                    )

                    val hourly = forecast.list.take(24).map { 
                        LocalForecastItem(it.dt, it.main.temp, it.weather[0].icon)
                    }
                    val daily = forecast.list.filterIndexed { index, _ -> index % 8 == 0 }.map { 
                        LocalForecastItem(it.dt, it.main.temp, it.weather[0].icon)
                    }

                    _uiState.value = HomeUiState.Success(
                        currentWeather = weatherEntity,
                        hourlyForecast = hourly,
                        dailyForecast = daily,
                        isOnline = true,
                        units = units,
                        windSpeedUnit = windSpeedUnit,
                        language = lang,
                        lastUpdate = _lastSyncTime.value
                    )
                } else {
                    _uiState.value = HomeUiState.Error(UiText.StringResource(R.string.failed_fetch_weather))
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(mapThrowableToUiText(e))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun mapThrowableToUiText(e: Throwable): UiText {
        return when (e) {
            is IOException -> UiText.StringResource(R.string.no_internet_title)
            else -> UiText.StringResource(R.string.failed_fetch_weather)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FavoriteDetailsViewModelFactory(
    private val app: Application,
    private val lat: Double,
    private val lon: Double,
    private val cityName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = WeatherDatabase.getInstance(app)
        val localDataSource = WeatherLocalDataSourceImpl(database.weatherDao(), app)
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.api)
        val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
        val connectivityObserver = NetworkConnectivityObserver(app)
        return FavoriteDetailsViewModel(repository, connectivityObserver, lat, lon, cityName) as T
    }
}
