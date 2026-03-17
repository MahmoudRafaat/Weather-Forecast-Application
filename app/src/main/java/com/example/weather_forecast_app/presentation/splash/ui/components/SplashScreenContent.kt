package com.example.weather_forecast_app.presentation.splash.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.airbnb.lottie.compose.*
import com.example.weather_forecast_app.R

@Composable
fun SplashScreenContent(
    screenHeight: Dp,
    screenWidth: Dp,
    onNavigateToHome: () -> Unit = {}
) {
    val backgroundLottie by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.snowing)
    )
    val mainLottie by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.weather)
    )
    val progress by animateLottieCompositionAsState(
        composition = mainLottie,
        iterations = 1,
        restartOnPlay = false,
    )
    LaunchedEffect(progress) {
        if (progress == 1f) {
            onNavigateToHome()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundAnimation(backgroundLottie)

        MainWeatherAnimation(
            composition = mainLottie,
            progress = { progress },
            modifier = Modifier
                .size(screenWidth)
                .align(Alignment.TopCenter)
                .padding(top = screenHeight * 0.1f)
        )

        TitleSection(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
