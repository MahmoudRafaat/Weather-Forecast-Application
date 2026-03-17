package com.example.weather_forecast_app.presentation.settings.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.presentation.navigation.ScreenRoute
import com.example.weather_forecast_app.presentation.settings.ui.components.*
import com.example.weather_forecast_app.presentation.settings.viewmodel.SettingsViewModel
import com.example.weather_forecast_app.presentation.settings.viewmodel.SettingsViewModelFactory
import com.example.weather_forecast_app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController : NavController
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(app))

    val locationMode by viewModel.locationMode.collectAsStateWithLifecycle()
    val units by viewModel.units.collectAsStateWithLifecycle()
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        if (isGranted) {
            viewModel.updateLocationMode("gps")
        } else {
            showPermissionDeniedDialog = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToMap.collect {
            navController.navigate(ScreenRoute.MapScreenRoute())
        }
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
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WeatherTheme.colors.backgroundGradient)
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(visible = !isOnline) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
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
                            text = stringResource(R.string.offline_settings_warning),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            SettingsSectionTitle(stringResource(R.string.location).uppercase())
            SettingsCard(enabled = isOnline) {
                LocationRadioItem(
                    title = stringResource(R.string.gps),
                    icon = Icons.Default.LocationOn,
                    selected = locationMode == "gps",
                    onClick = {
                        val hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (hasPermissions) {
                            viewModel.updateLocationMode("gps")
                        } else {
                            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    },
                    enabled = isOnline
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                
                LocationRadioItem(
                    title = stringResource(R.string.map),
                    icon = Icons.Default.Map,
                    selected = locationMode == "map",
                    onClick = { viewModel.onNavigateToMap() },
                    enabled = isOnline
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            SettingsSectionTitle(stringResource(R.string.theme).uppercase())
            SettingsCard(enabled = isOnline) {
                MultiOptionSelector(
                    options = listOf(
                        stringResource(R.string.light) to "light",
                        stringResource(R.string.dark) to "dark",
                        stringResource(R.string.system_default) to "system"
                    ),
                    selectedOption = themeMode,
                    onOptionSelected = { viewModel.updateThemeMode(it) },
                    enabled = isOnline
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSectionTitle(stringResource(R.string.units).uppercase())
            SettingsCard(enabled = isOnline) {
                MultiOptionSelector(
                    options = listOf(
                        stringResource(R.string.metric) + " (°C)" to "metric",
                        stringResource(R.string.imperial) + " (°F)" to "imperial",
                        stringResource(R.string.standard) + " (K)" to "standard"
                    ),
                    selectedOption = units,
                    onOptionSelected = { viewModel.updateUnits(it) },
                    enabled = isOnline
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSectionTitle(stringResource(R.string.wind_speed_unit).uppercase())
            SettingsCard(enabled = isOnline) {
                MultiOptionSelector(
                    options = listOf(
                        stringResource(R.string.meter_sec) to "m/s",
                        stringResource(R.string.miles_hour) to "mph"
                    ),
                    selectedOption = windSpeedUnit,
                    onOptionSelected = { viewModel.updateWindSpeedUnit(it) },
                    enabled = isOnline
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSectionTitle(stringResource(R.string.language).uppercase())
            SettingsCard(enabled = isOnline) {
                LanguageSelectionItem(
                    title = stringResource(R.string.english),
                    icon = Icons.Default.Language,
                    selected = language == "en",
                    onClick = { viewModel.updateLanguage("en") },
                    enabled = isOnline
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                LanguageSelectionItem(
                    title = stringResource(R.string.arabic),
                    icon = Icons.Default.Translate,
                    selected = language == "ar",
                    onClick = { viewModel.updateLanguage("ar") },
                    enabled = isOnline
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
