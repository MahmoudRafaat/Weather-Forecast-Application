package com.example.weather_forecast_app.presentation.map.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast_app.R
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun TapTip(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(bottom = 40.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            stringResource(R.string.tap_map_to_set_location),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp
        )
    }
}
