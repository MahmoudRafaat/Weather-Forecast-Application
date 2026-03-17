package com.example.weather_forecast_app.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class CustomColors(
    val backgroundGradient: Brush,
    val cardBackground: Color,
    val cardBorder: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        backgroundGradient = Brush.verticalGradient(listOf(Color.Black, Color.Black)),
        cardBackground = Color.DarkGray,
        cardBorder = Color.LightGray
    )
}

object WeatherTheme {
    val colors: CustomColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = SolidPurple,
    secondary = SolidLavender,
    tertiary = SolidMagenta,
    background = DarkBackgroundEnd,
    surface = DarkBackgroundStart,
    onPrimary = White,
    onBackground = White,
    onSurface = White,
    primaryContainer = SolidMagenta.copy(alpha = 0.2f),
    onPrimaryContainer = SolidMagenta,
    secondaryContainer = SolidPurple.copy(alpha = 0.2f),
    onSecondaryContainer = SolidLavender,
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0)
)

private val LightColorScheme = lightColorScheme(
    primary = SolidPurple,
    secondary = SolidPurple.copy(alpha = 0.7f),
    tertiary = SolidMagenta,
    background = LightBackgroundEnd,
    surface = LightBackgroundStart,
    onPrimary = White,
    onBackground = Black,
    onSurface = Black,
    primaryContainer = SolidMagenta.copy(alpha = 0.15f),
    onPrimaryContainer = SolidMagenta,
    secondaryContainer = SolidPurple.copy(alpha = 0.1f),
    onSecondaryContainer = SolidPurple,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F)
)

@Composable
fun WeatherForecastAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    val customColors = if (darkTheme) {
        CustomColors(
            backgroundGradient = Brush.verticalGradient(listOf(DarkBackgroundStart, DarkBackgroundEnd)),
            cardBackground = DarkCardBackground,
            cardBorder = DarkCardBorder
        )
    } else {
        CustomColors(
            backgroundGradient = Brush.verticalGradient(listOf(LightBackgroundStart, LightBackgroundEnd)),
            cardBackground = LightCardBackground,
            cardBorder = LightCardBorder
        )
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
