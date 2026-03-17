package com.example.weather_forecast_app.presentation.favoritedetails.ui

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.presentation.favoritedetails.ui.components.NoInternetView
import com.example.weather_forecast_app.presentation.favoritedetails.viewmodel.FavoriteDetailsViewModel
import com.example.weather_forecast_app.presentation.favoritedetails.viewmodel.FavoriteDetailsViewModelFactory
import com.example.weather_forecast_app.presentation.home.ui.components.HomeContent
import com.example.weather_forecast_app.presentation.home.viewmodel.HomeUiState
import com.example.weather_forecast_app.presentation.theme.SolidMagenta
import com.example.weather_forecast_app.presentation.theme.WeatherTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoriteDetailsScreen(
    navController: NavController,
    cityName: String,
    lan: Double,
    lon: Double,
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val viewModel: FavoriteDetailsViewModel = viewModel(
        factory = FavoriteDetailsViewModelFactory(app, lan, lon, cityName)
    )
    
    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { navController.popBackStack() }
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val noInternet by viewModel.noInternet.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.retry() }
    )

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onBackClicked() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cancel),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = cityName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WeatherTheme.colors.backgroundGradient)
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            if (noInternet) {
                NoInternetView(onRetry = { viewModel.retry() })
            } else {
                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = SolidMagenta
                        )
                    }
                    is HomeUiState.Success -> {
                        Column {
                            AnimatedVisibility(visible = !state.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f))
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.CloudOff,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.offline_mode),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                            HomeContent(
                                currentWeather = state.currentWeather,
                                hourlyForecast = state.hourlyForecast,
                                dailyForecast = state.dailyForecast,
                                lastUpdate = state.lastUpdate,
                                windSpeedUnit = state.windSpeedUnit,
                                currentSystem = state.units
                            )
                        }
                    }
                    is HomeUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = state.message.asString(),
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.retry() },
                                    colors = ButtonDefaults.buttonColors(containerColor = SolidMagenta)
                                ) {
                                    Text(stringResource(R.string.retry))
                                }
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = false, 
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = SolidMagenta
            )
        }
    }
}
