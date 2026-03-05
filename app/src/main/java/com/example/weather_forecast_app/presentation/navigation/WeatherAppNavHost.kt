package com.example.weather_forecast_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather_forecast_app.presentation.home.ui.HomeScreen
import com.example.weather_forecast_app.presentation.splash.SplashScreen

@Composable
fun WeatherAppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.SplashScreenRoute
    ) {
        composable<ScreenRoute.SplashScreenRoute> {
            SplashScreen(onNavigateToHome = {
                navController.navigate(ScreenRoute.HomeScreenRoute) {
                    popUpTo(ScreenRoute.SplashScreenRoute) { inclusive = true }
                }
            })
        }
        composable<ScreenRoute.HomeScreenRoute> {
            HomeScreen()
        }
    }
}
