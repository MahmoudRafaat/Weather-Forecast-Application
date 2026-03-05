package com.example.weather_forecast_app.presentation.splash


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.weather_forecast_app.ui.theme.AccentYellow
import com.example.weather_forecast_app.ui.theme.PrimaryDark
import com.example.weather_forecast_app.ui.theme.backgroundGradient
import com.airbnb.lottie.LottieComposition


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        SplashScreenContent(maxHeight, maxWidth, onNavigateToHome)
    }
}

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

@Composable
private fun BackgroundAnimation(composition: LottieComposition?) {
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun MainWeatherAnimation(
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

@Composable
private fun TitleSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.weather),
            style = typography.displayLarge,
            color = PrimaryDark,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.forecasts),
            style = typography.displayLarge,
            color = AccentYellow
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview(){
    SplashScreen()
}
