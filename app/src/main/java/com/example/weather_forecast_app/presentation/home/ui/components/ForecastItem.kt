package com.example.weather_forecast_app.presentation.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.weather.model.LocalForecastItem
import com.example.weather_forecast_app.presentation.theme.WeatherTheme
import com.example.weather_forecast_app.presentation.theme.selectedGlowGradient
import com.example.weather_forecast_app.utils.WeatherUtils
import kotlin.math.roundToInt

@Composable
fun ForecastItem(item: LocalForecastItem, isSelected: Boolean, dateFormat: String, tempUnit: String) {
    val border = if (isSelected) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)

    Column(
        modifier = Modifier
            .width(85.dp)
            .height(160.dp)
            .background(
                brush = if (isSelected) selectedGlowGradient else Brush.verticalGradient(listOf(WeatherTheme.colors.cardBackground, WeatherTheme.colors.cardBackground)),
                shape = RoundedCornerShape(40.dp)
            )
            .border(1.dp, border, RoundedCornerShape(40.dp))
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = WeatherUtils.formatUnixTimestamp(item.dt, dateFormat),
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Icon(
            painter = painterResource(id = WeatherUtils.getWeatherIcon(item.icon, isBig = false)),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = Color.Unspecified
        )
        Text(
            text = stringResource(R.string.temperature_format, WeatherUtils.formatNumber(item.temp.roundToInt()), tempUnit),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    }
}
