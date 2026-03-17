package com.example.weather_forecast_app.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Neutrals
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Gray = Color(0xFF8E8E93)

// Weather Solid Colors
val SolidPurple = Color(0xFF48319D)
val SolidDarkBlue = Color(0xFF1F1D47)
val SolidMagenta = Color(0xFFC427FB)
val SolidLavender = Color(0xFFE0D9FF)
val AccentYellow = Color(0xFFE2B93B)

// Dark Theme Colors
val DarkBackgroundStart = Color(0xFF2E335A)
val DarkBackgroundEnd = Color(0xFF1C1B33)
val DarkCardBackground = Color(0xFF2E335A).copy(alpha = 0.5f)
val DarkCardBorder = Color(0xFFFFFFFF).copy(alpha = 0.2f)

// Light Theme Colors
val LightBackgroundStart = Color(0xFFF2F2F7)
val LightBackgroundEnd = Color(0xFFFFFFFF)
val LightCardBackground = Color(0xFFFFFFFF).copy(alpha = 0.7f)
val LightCardBorder = Color(0xFF000000).copy(alpha = 0.1f)

// Brushes (Deprecated: Use dynamic variants in Theme)
val backgroundGradient = Brush.verticalGradient(
    colors = listOf(DarkBackgroundStart, DarkBackgroundEnd)
)

val selectedGlowGradient = Brush.verticalGradient(
    colors = listOf(SolidMagenta, SolidLavender)
)
