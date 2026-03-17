package com.example.weather_forecast_app.presentation.splash


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.alpha

import androidx.compose.ui.text.font.FontWeight
import com.example.weather_forecast_app.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp

import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.airbnb.lottie.compose.animateLottieCompositionAsState

import com.airbnb.lottie.LottieComposition
import com.example.weather_forecast_app.presentation.splash.ui.components.SplashScreenContent
import com.example.weather_forecast_app.presentation.theme.*


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(WeatherTheme.colors.backgroundGradient)
    ) {
        SplashScreenContent(maxHeight, maxWidth, onNavigateToHome)
    }
}






