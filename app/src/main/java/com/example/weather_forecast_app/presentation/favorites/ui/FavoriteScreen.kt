package com.example.weather_forecast_app.presentation.favorites.ui

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.weather.model.FavoriteLocation
import com.example.weather_forecast_app.presentation.favorites.ui.components.EmptyFavorites
import com.example.weather_forecast_app.presentation.favorites.ui.components.FavoriteItem
import com.example.weather_forecast_app.presentation.favorites.viewmodel.FavoriteUiState
import com.example.weather_forecast_app.presentation.favorites.viewmodel.FavoriteViewModel
import com.example.weather_forecast_app.presentation.favorites.viewmodel.FavoriteViewModelFactory
import com.example.weather_forecast_app.presentation.navigation.ScreenRoute
import com.example.weather_forecast_app.presentation.theme.*

@Composable
fun FavoriteScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val viewModel: FavoriteViewModel = viewModel(factory = FavoriteViewModelFactory(app))

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var locationToDelete by remember { mutableStateOf<FavoriteLocation?>(null) }

    val isOnline = (uiState as? FavoriteUiState.Success)?.isOnline ?: true

    LaunchedEffect(Unit) {
        viewModel.navigateToMap.collect {
            navController.navigate(ScreenRoute.MapScreenRoute(true))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToDetails.collect { location ->
            navController.navigate(
                ScreenRoute.FavoriteDetailsScreenRoute(
                    location.lat,
                    location.lon,
                    location.cityName
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.showDeleteConfirmation.collect { location ->
            locationToDelete = location
            showDeleteDialog = true
        }
    }

    if (showDeleteDialog && locationToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                locationToDelete = null
            },
            title = { Text(stringResource(R.string.delete_location), color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    stringResource(R.string.delete_confirmation, locationToDelete?.cityName ?: ""),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        locationToDelete?.let { viewModel.deleteFavorite(it) }
                        showDeleteDialog = false
                        locationToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    locationToDelete = null
                }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WeatherTheme.colors.backgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.statusBarsPadding())
            
            Text(
                text = stringResource(R.string.favorites),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            AnimatedVisibility(visible = !isOnline) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.offline_favorites_warning),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            when (val state = uiState) {
                is FavoriteUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SolidMagenta)
                    }
                }
                is FavoriteUiState.Success -> {
                    if (state.favorites.isEmpty()) {
                        EmptyFavorites()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.favorites, key = { it.id }) { location ->
                                FavoriteItem(
                                    location = location,
                                    onClick = { viewModel.onFavoriteClicked(location) },
                                    onDeleteClick = { viewModel.onDeleteRequest(location) }
                                )
                            }
                        }
                    }
                }
                is FavoriteUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message.asString(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.onAddFavoriteClicked() },
            containerColor = if (isOnline) SolidMagenta else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.AddLocation, contentDescription = stringResource(R.string.add_favorite))
        }
    }
}
