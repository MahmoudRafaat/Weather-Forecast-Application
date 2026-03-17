package com.example.weather_forecast_app.presentation.splash.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants

@Composable
 fun BackgroundAnimation(composition: LottieComposition?) {
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f),
        contentScale = ContentScale.FillHeight
    )
}

@Composable
 fun MainWeatherAnimation(
    composition: LottieComposition?,
    progress: () -> Float,
    modifier: Modifier = Modifier
) {
    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier
    )
}