package com.example.weather_forecast_app.presentation.alerts.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.db.WeatherDatabase
import com.example.weather_forecast_app.data.network.RetrofitHelper
import com.example.weather_forecast_app.data.weather.datasource.local.WeatherLocalDataSourceImpl
import com.example.weather_forecast_app.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.data.weather.repository.WeatherRepository
import com.example.weather_forecast_app.data.weather.repository.WeatherRepositoryImpl
import com.example.weather_forecast_app.services.WeatherAlertScheduler
import com.example.weather_forecast_app.utils.UiText
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

class AlertViewModel(private val repository: WeatherRepository, private val scheduler: WeatherAlertScheduler) : ViewModel() {

    val uiState: StateFlow<AlertUiState> = repository.getAllAlerts()
        .map<List<WeatherAlert>, AlertUiState> { AlertUiState.Success(it) }
        .onStart { emit(AlertUiState.Loading) }
        .catch { e -> emit(AlertUiState.Error(mapThrowableToUiText(e))) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AlertUiState.Loading)

    private val _errorEvent = MutableSharedFlow<UiText>()
    val errorEvent = _errorEvent.asSharedFlow()

    private fun mapThrowableToUiText(e: Throwable): UiText {
        return when (e) {
            is IOException -> UiText.StringResource(R.string.no_internet_title)
            else -> UiText.StringResource(R.string.unknown_error)
        }
    }

    private val _showDeleteConfirmation = MutableSharedFlow<WeatherAlert>()
    val showDeleteConfirmation = _showDeleteConfirmation.asSharedFlow()

    fun addAlert(startTime: Long, endTime: Long, type: String) {
        viewModelScope.launch {
            try {
                val alert = WeatherAlert(startTime = startTime, endTime = endTime, type = type)
                val id = repository.insertAlert(alert)
                scheduler.schedule(alert.copy(id = id.toInt()))
            } catch (e: Exception) {
                _errorEvent.emit(UiText.StringResource(R.string.unknown_error))
            }
        }
    }

    fun onDeleteRequest(alert: WeatherAlert) {
        viewModelScope.launch {
            _showDeleteConfirmation.emit(alert)
        }
    }

    fun deleteAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            try {
                repository.deleteAlert(alert)
                scheduler.cancel(alert.id)
            } catch (e: Exception) {
                _errorEvent.emit(UiText.StringResource(R.string.unknown_error))
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AlertViewModelFactory(
    private val app: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = WeatherDatabase.getInstance(app)
        val localDataSource = WeatherLocalDataSourceImpl(database.weatherDao(), app)
        val remoteDataSource = WeatherRemoteDataSourceImpl(RetrofitHelper.api)
        val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
        val scheduler = WeatherAlertScheduler(app)
        return AlertViewModel(repository, scheduler) as T
    }
}
