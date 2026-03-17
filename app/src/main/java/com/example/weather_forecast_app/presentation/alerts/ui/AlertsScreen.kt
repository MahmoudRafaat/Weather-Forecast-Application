package com.example.weather_forecast_app.presentation.alerts.ui

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.weather.model.WeatherAlert
import com.example.weather_forecast_app.presentation.alerts.ui.components.AddAlertDialog
import com.example.weather_forecast_app.presentation.alerts.ui.components.AlertItem
import com.example.weather_forecast_app.presentation.alerts.viewmodel.AlertUiState
import com.example.weather_forecast_app.presentation.alerts.viewmodel.AlertViewModel
import com.example.weather_forecast_app.presentation.alerts.viewmodel.AlertViewModelFactory
import com.example.weather_forecast_app.presentation.theme.*
import com.example.weather_forecast_app.utils.UiText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val viewModel: AlertViewModel = viewModel(factory = AlertViewModelFactory(app))

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var alertToDelete by remember { mutableStateOf<WeatherAlert?>(null) }

    LaunchedEffect(Unit) {
        viewModel.showDeleteConfirmation.collect { alert ->
            alertToDelete = alert
            showDeleteDialog = true
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showAddDialog = true
        }
    }

    if (showDeleteDialog && alertToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                alertToDelete = null
            },
            title = { Text(stringResource(R.string.delete_alert), color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    stringResource(R.string.delete_alert_confirmation),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        alertToDelete?.let { viewModel.deleteAlert(it) }
                        showDeleteDialog = false
                        alertToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    alertToDelete = null
                }) {
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
                title = { Text(stringResource(R.string.alerts), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                            showAddDialog = true
                        } else {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        showAddDialog = true
                    }
                },
                containerColor = SolidMagenta,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.AddAlert, contentDescription = stringResource(R.string.add_alert))
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WeatherTheme.colors.backgroundGradient)
                .padding(padding)
        ) {
            when (val state = uiState) {
                is AlertUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SolidMagenta)
                    }
                }
                is AlertUiState.Success -> {
                    if (state.alerts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.no_alerts), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.alerts, key = { it.id }) { alert ->
                                AlertItem(alert = alert, onDeleteClick = { viewModel.onDeleteRequest(alert) })
                            }
                        }
                    }
                }
                is AlertUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val message = when (val uiText = state.message) {
                            is UiText.DynamicString -> uiText.value
                            is UiText.StringResource -> stringResource(uiText.resId, *uiText.args)
                        }
                        Text(text = message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAlertDialog(
            onDismiss = { showAddDialog = false },
            onSave = { startTime, endTime, type ->
                viewModel.addAlert(startTime, endTime, type)
                showAddDialog = false
            }
        )
    }
}
