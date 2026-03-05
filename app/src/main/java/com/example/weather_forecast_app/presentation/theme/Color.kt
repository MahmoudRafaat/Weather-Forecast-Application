package com.example.weather_forecast_app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Light Theme Neutrals
val PrimaryLight = Color(0xFF000000)
val SecondaryLight = Color(0xFF3C3C43) // 60% opacity suggested in image
val TertiaryLight = Color(0xFF3C3C43)  // 30% opacity suggested in image

// Dark Theme Neutrals
val PrimaryDark = Color(0xFFFFFFFF)
val SecondaryDark = Color(0xFFEBEBF5)

// Weather Solid Colors
val SolidPurple = Color(0xFF48319D)
val SolidDarkBlue = Color(0xFF1F1D47)
val SolidMagenta = Color(0xFFC427FB)
val SolidLavender = Color(0xFFE0D9FF)
val AccentYellow = Color(0xFFE2B93B)

// Gradient Points (From "Linear" styles)
val BackgroundGradientStart = Color(0xFF2E335A)
val BackgroundGradientEnd = Color(0xFF1C1B33)
val SecondaryGradientStart = Color(0xFF5936B4)
val SecondaryGradientEnd = Color(0xFF362A84)

val backgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF2E3A8C),
        Color(0xFF6A3FA0),
        Color(0xFFB03A9F))
)
