package com.example.weather_forecast_app.presentation.map.ui

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weather_forecast_app.presentation.map.ui.components.*
import com.example.weather_forecast_app.presentation.map.viewmodel.MapViewModel
import com.example.weather_forecast_app.presentation.map.viewmodel.MapViewModelFactory
import com.example.weather_forecast_app.presentation.theme.WeatherTheme
import org.osmdroid.config.Configuration

@Composable
fun MapScreen(
    navController: NavController,
    isFavoriteMode: Boolean,
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val viewModel: MapViewModel = viewModel(factory = MapViewModelFactory(app))
    val cityName by viewModel.cityName.collectAsStateWithLifecycle()
    val selectedPoint by viewModel.selectedPoint.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    Scaffold(
        topBar = {
            MapTopBar(
                onBack = { viewModel.onBackClicked() },
                onConfirm = { viewModel.confirmLocation(isFavoriteMode) },
                showConfirm = selectedPoint != null,
                isEnabled = isOnline
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WeatherTheme.colors.backgroundGradient)
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = !isOnline,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    OfflineBanner()
                }
                
                Box(modifier = Modifier.weight(1f)) {
                    OsmMapView(
                        selectedPoint = selectedPoint,
                        cityName = cityName,
                        onPointSelected = { viewModel.onPointSelected(context, it) }
                    )

                    if (selectedPoint != null) {
                        SelectedCityCard(
                            cityName = cityName,
                            onConfirm = { viewModel.confirmLocation(isFavoriteMode) },
                            isEnabled = isOnline,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    } else {
                        TapTip(modifier = Modifier.align(Alignment.BottomCenter))
                    }
                }
            }
        }
    }
}
