package com.example.weather_forecast_app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Mapping your "Weather Color Styles" to the Dark Theme
private val DarkColorScheme = darkColorScheme(
    primary = SolidPurple,
    secondary = SolidLavender,
    tertiary = SolidMagenta,
    background = SolidDarkBlue,
    surface = SolidDarkBlue,
    onPrimary = PrimaryDark,
    onBackground = PrimaryDark,
    onSurface = PrimaryDark
)

// Mapping your "Light" Neutrals to the Light Theme
private val LightColorScheme = lightColorScheme(
    primary = SolidPurple,
    secondary = SecondaryLight,
    tertiary = SolidMagenta,
    background = PrimaryDark, // White background for light mode
    surface = PrimaryDark,
    onPrimary = PrimaryDark,
    onBackground = PrimaryLight,
    onSurface = PrimaryLight
)

@Composable
fun WeatherForecastAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false so your specific weather colors are used instead of system colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // This uses the Typography you just defined
        content = content
    )
}