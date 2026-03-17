package com.example.weather_forecast_app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weather_forecast_app.presentation.alerts.ui.AlertsScreen
import com.example.weather_forecast_app.presentation.favoritedetails.ui.FavoriteDetailsScreen
import com.example.weather_forecast_app.presentation.favorites.ui.FavoriteScreen
import com.example.weather_forecast_app.presentation.home.ui.HomeScreen
import com.example.weather_forecast_app.presentation.map.ui.MapScreen
import com.example.weather_forecast_app.presentation.settings.ui.SettingsScreen
import com.example.weather_forecast_app.presentation.splash.SplashScreen
import com.example.weather_forecast_app.presentation.theme.SolidMagenta

@Composable
fun WeatherAppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.let { dest ->
        !dest.hasRoute<ScreenRoute.SplashScreenRoute>() && 
        !dest.hasRoute<ScreenRoute.MapScreenRoute>() &&
        dest.route?.contains("FavoriteDetailsScreenRoute") == false
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)),
                    tonalElevation = 8.dp
                ) {
                    val isHomeSelected = currentDestination.hierarchy.any { it.hasRoute<ScreenRoute.HomeScreenRoute>() }
                    val isFavoritesSelected = currentDestination.hierarchy.any { it.hasRoute<ScreenRoute.FavoriteScreenRoute>() }
                    val isAlertsSelected = currentDestination.hierarchy.any { it.hasRoute<ScreenRoute.AlertsScreenRoute>() }
                    val isSettingsSelected = currentDestination.hierarchy.any { it.hasRoute<ScreenRoute.SettingsScreenRoute>() }

                    NavigationBarItem(
                        selected = isHomeSelected,
                        onClick = {
                            if (!isHomeSelected) {
                                navController.navigate(ScreenRoute.HomeScreenRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(if (isHomeSelected) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SolidMagenta,
                            indicatorColor = SolidMagenta.copy(alpha = 0.1f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = isFavoritesSelected,
                        onClick = {
                            if (!isFavoritesSelected) {
                                navController.navigate(ScreenRoute.FavoriteScreenRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(if (isFavoritesSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SolidMagenta,
                            indicatorColor = SolidMagenta.copy(alpha = 0.1f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = isAlertsSelected,
                        onClick = {
                            if (!isAlertsSelected) {
                                navController.navigate(ScreenRoute.AlertsScreenRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(if (isAlertsSelected) Icons.Filled.Notifications else Icons.Outlined.Notifications, contentDescription = null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SolidMagenta,
                            indicatorColor = SolidMagenta.copy(alpha = 0.1f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = isSettingsSelected,
                        onClick = {
                            if (!isSettingsSelected) {
                                navController.navigate(ScreenRoute.SettingsScreenRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(if (isSettingsSelected) Icons.Filled.Settings else Icons.Outlined.Settings, contentDescription = null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SolidMagenta,
                            indicatorColor = SolidMagenta.copy(alpha = 0.1f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.SplashScreenRoute,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<ScreenRoute.SplashScreenRoute> {
                SplashScreen(onNavigateToHome = {
                    navController.navigate(ScreenRoute.HomeScreenRoute) {
                        popUpTo(ScreenRoute.SplashScreenRoute) { inclusive = true }
                    }
                })
            }
            composable<ScreenRoute.HomeScreenRoute> {
                HomeScreen(navController)
            }
            composable<ScreenRoute.SettingsScreenRoute> {
                SettingsScreen(navController)
            }
            composable<ScreenRoute.FavoriteScreenRoute> {
                FavoriteScreen(navController)
            }
            composable<ScreenRoute.AlertsScreenRoute> {
                AlertsScreen(navController)
            }
            composable<ScreenRoute.FavoriteDetailsScreenRoute> { backStackEntry ->
                val route: ScreenRoute.FavoriteDetailsScreenRoute = backStackEntry.toRoute()
                FavoriteDetailsScreen(
                    navController = navController,
                    cityName = route.cityName,
                    lan = route.lat,
                    lon = route.lon
                )
            }
            composable<ScreenRoute.MapScreenRoute> { backStackEntry ->
                val route: ScreenRoute.MapScreenRoute = backStackEntry.toRoute()
                MapScreen(
                    isFavoriteMode = route.isFavoriteMode,
                    navController = navController,
                )
            }
        }
    }
}
