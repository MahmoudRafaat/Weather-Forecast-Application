package com.example.weather_forecast_app.presentation.home.ui

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.presentation.favoritedetails.ui.components.NoInternetView
import com.example.weather_forecast_app.presentation.home.ui.components.HomeContent
import com.example.weather_forecast_app.presentation.home.ui.components.PermissionDeniedView
import com.example.weather_forecast_app.presentation.home.viewmodel.HomeUiState
import com.example.weather_forecast_app.presentation.home.viewmodel.HomeViewModel
import com.example.weather_forecast_app.presentation.home.viewmodel.HomeViewModelFactory
import com.example.weather_forecast_app.presentation.theme.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(app))

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val noInternet by viewModel.noInternet.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val locationMode by viewModel.locationMode.collectAsStateWithLifecycle()
    
    val lifecycleOwner = LocalLifecycleOwner.current
    var showGPSDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        if (isGranted) {
            permissionDenied = false
            showPermissionDeniedDialog = false
            if (!viewModel.isLocationEnabled()) {
                showGPSDialog = true
            } else {
                viewModel.fetchWeather()
            }
        } else {
            permissionDenied = true
            showPermissionDeniedDialog = true
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { 
            if (locationMode == "gps") {
                val hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                   ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                
                if (hasPermissions) {
                    if (!viewModel.isLocationEnabled()) {
                        showGPSDialog = true
                    } else {
                        viewModel.fetchWeather()
                    }
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            } else {
                viewModel.fetchWeather()
            }
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (locationMode == "gps") {
                    val hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                       ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    
                    if (hasPermissions) {
                        permissionDenied = false
                        showPermissionDeniedDialog = false
                        if (!viewModel.isLocationEnabled()) {
                            showGPSDialog = true
                        } else {
                            showGPSDialog = false
                            viewModel.fetchWeather()
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(locationMode) {
        if (locationMode == "gps") {
            val hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                               ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            
            if (!hasPermissions) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    if (showGPSDialog) {
        AlertDialog(
            onDismissRequest = { showGPSDialog = false },
            title = { Text(stringResource(R.string.gps_disabled), color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(stringResource(R.string.gps_required), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.enableLocation()
                        showGPSDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SolidMagenta)
                ) {
                    Text(stringResource(R.string.settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showGPSDialog = false }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            title = { Text(stringResource(R.string.location_permission_required), color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(stringResource(R.string.location_permission_desc), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDeniedDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SolidMagenta)
                ) {
                    Text(stringResource(R.string.go_to_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDeniedDialog = false }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                NoInternetView (onRetry = { viewModel.fetchWeather() })
            } else {
                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        if (permissionDenied && locationMode == "gps") {
                            PermissionDeniedView(
                                onRetry = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                onGoToSettings = {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = SolidMagenta
                            )
                        }
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
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.error, state.message),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge)
                            Button(
                                onClick = { viewModel.fetchWeather() },
                                colors = ButtonDefaults.buttonColors(containerColor = SolidMagenta)
                            ) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = false, // Arrow shows while pulling, but indicator spinner is hidden after release
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = SolidMagenta
            )
        }
    }
}
