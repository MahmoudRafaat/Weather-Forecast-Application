package com.example.weather_forecast_app.presentation.home.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.data.weather.model.LocalForecastItem

@Composable
fun ForecastSection(title: String, items: List<LocalForecastItem>, dateFormat: String, tempUnit: String) {
    val today = stringResource(R.string.today)
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(items) { item ->
                ForecastItem(
                    item = item,
                    isSelected = items.indexOf(item) == 0 && title == today,
                    dateFormat = dateFormat,
                    tempUnit = tempUnit
                )
            }
        }
    }
}
