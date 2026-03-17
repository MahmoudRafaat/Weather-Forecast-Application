package com.example.weather_forecast_app.presentation.home.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.db.WeatherDatabase
import com.example.weather_forecast_app.data.network.RetrofitHelper
import com.example.weather_forecast_app.data.weather.datasource.local.WeatherLocalDataSourceImpl
import com.example.weather_forecast_app.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.example.weather_forecast_app.data.weather.repository.WeatherRepository
import com.example.weather_forecast_app.data.weather.repository.WeatherRepositoryImpl
import com.example.weather_forecast_app.utils.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class HomeViewModel(
    private val repository: WeatherRepository,
    private val locationProvider: LocationProvider,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _noInternet = MutableStateFlow(false)
    val noInternet = _noInternet.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("")

    val locationMode: StateFlow<String> = repository.getLocationMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")

    private val connectivityStatus = connectivityObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityObserver.Status.Unavailable)

    private data class ExtraParams(
        val units: String,
        val windSpeedUnit: String,
        val lang: String,
        val refreshing: Boolean,
        val syncTime: String
    )

    private val extraParamsFlow = combine(
        repository.getUnits(),
        repository.getWindSpeedUnit(),
        repository.getLanguage(),
        _isRefreshing,
        _lastSyncTime
    ) { units, windUnit, lang, refreshing, syncTime ->
        ExtraParams(units, windUnit, lang, refreshing, syncTime)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getCurrentWeatherLocal(),
        repository.getForecastLocal(),
        connectivityStatus,
        extraParamsFlow
    ) { weather, forecast, status, extras ->
        val isOnline = status == ConnectivityObserver.Status.Available
        
        if (extras.refreshing || weather == null || forecast == null) {
            HomeUiState.Loading
        } else {
            HomeUiState.Success(
                currentWeather = weather,
                hourlyForecast = forecast.hourlyList,
                dailyForecast = forecast.dailyList,
                isOnline = isOnline,
                units = extras.units,
                windSpeedUnit = extras.windSpeedUnit,
                language = extras.lang,
                lastUpdate = extras.syncTime
            )
        }
    }.catch { e ->
        emit(HomeUiState.Error(mapThrowableToUiText(e)))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState.Loading)

    private fun mapThrowableToUiText(e: Throwable): UiText {
        return when (e) {
            is IOException -> UiText.StringResource(R.string.no_internet_title)
            else -> UiText.StringResource(R.string.failed_fetch_weather)
        }
    }

    init {
        observeSettingsForRefresh()
        observeConnectivity()
    }

    private fun observeConnectivity() {
        connectivityStatus.onEach { status ->
            val isOnline = status == ConnectivityObserver.Status.Available
            if (isOnline) {
                _noInternet.value = false
                fetchWeather()
            } else {
                val hasLocalData = repository.getCurrentWeatherLocal().first() != null
                if (!hasLocalData) {
                    _noInternet.value = true
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun observeSettingsForRefresh() {
        val settingsFlow = combine(
            repository.getLocationMode(),
            repository.getUnits(),
            repository.getLanguage(),
            repository.getManualLocation(),
            repository.getManualCityName()
        ) { mode, units, lang, manualLoc, manualCity ->
            RefreshParams(mode, units, lang, manualLoc, manualCity, ConnectivityObserver.Status.Unavailable)
        }

        settingsFlow.combine(connectivityStatus) { params, status ->
            params.copy(status = status)
        }
        .distinctUntilChanged()
        .onEach { params ->
            if (params.status == ConnectivityObserver.Status.Available) {
                viewModelScope.launch {
                    val coords = if (params.mode == "gps") {
                        locationProvider.getCurrentLocation()
                    } else {
                        params.manualLoc
                    }
                    

                    coords?.let { refreshWeather(it.first, it.second, params.units, params.lang, null) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private data class RefreshParams(
        val mode: String,
        val units: String,
        val lang: String,
        val manualLoc: Pair<Double, Double>?,
        val manualCity: String?,
        val status: ConnectivityObserver.Status
    )

    fun isLocationEnabled(): Boolean = locationProvider.isLocationEnabled()
    fun enableLocation() = locationProvider.enableLocation()

    fun fetchWeather() {
        viewModelScope.launch {
            if (connectivityStatus.value != ConnectivityObserver.Status.Available) {
                _isRefreshing.value = false
                return@launch
            }
            
            _isRefreshing.value = true
            try {
                val mode = repository.getLocationMode().first()
                val units = repository.getUnits().first()
                val lang = repository.getLanguage().first()
                
                val coords = if (mode == "gps") {
                    locationProvider.getCurrentLocation()
                } else {
                    repository.getManualLocation().first()
                }
                
                if (coords != null) {
                    refreshWeather(coords.first, coords.second, units, lang, null)
                    updateSyncTime()
                }
            } catch (e: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun updateSyncTime() {
        _lastSyncTime.value = WeatherUtils.formatTime(System.currentTimeMillis(), "hh:mm a")
    }

    private suspend fun refreshWeather(lat: Double?, lon: Double?, units: String, lang: String, cityName: String?) {
        if (lat == null || lon == null) return
        _isRefreshing.value = true
        try {
            repository.refreshWeather(lat, lon, units, lang, cityName)
            _noInternet.value = false
        } catch (e: Exception) {
        } finally {
            _isRefreshing.value = false
        }
    }
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = WeatherDatabase.getInstance(app)
        val localDataSource = WeatherLocalDataSourceImpl(database.weatherDao(), app)
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.api)
        val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
        val locationProvider = LocationProviderImpl(app)
        val connectivityObserver = NetworkConnectivityObserver(app)

        return HomeViewModel(repository, locationProvider, connectivityObserver) as T
    }
}
