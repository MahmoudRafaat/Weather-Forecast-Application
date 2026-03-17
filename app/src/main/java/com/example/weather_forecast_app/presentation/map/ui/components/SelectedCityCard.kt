package com.example.weather_forecast_app.presentation.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.presentation.theme.Gray
import com.example.weather_forecast_app.presentation.theme.SolidMagenta
import com.example.weather_forecast_app.presentation.theme.WeatherTheme

@Composable
fun SelectedCityCard(
    cityName: String?,
    onConfirm: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WeatherTheme.colors.cardBackground.copy(alpha = 0.95f)),
        border = BorderStroke(1.dp, WeatherTheme.colors.cardBorder)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = stringResource(R.string.selected_city),
                    style = MaterialTheme.typography.labelSmall,
                    color = SolidMagenta,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = cityName ?: stringResource(R.string.searching),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Button(
                onClick = onConfirm,
                enabled = isEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SolidMagenta,
                    disabledContainerColor = Gray,
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(stringResource(R.string.confirm))
            }
        }
    }
}
