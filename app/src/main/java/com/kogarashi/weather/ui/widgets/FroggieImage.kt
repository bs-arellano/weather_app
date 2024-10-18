package com.kogarashi.weather.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kogarashi.weather.R
import com.kogarashi.weather.domain.getWeatherInterpretation

@Composable
fun FroggieImage(weatherCode: Int){
    val id = when(weatherCode){
        0 -> R.drawable.froggie_sunny
        1 -> R.drawable.froggie_mostly_sunny
        3,103 -> R.drawable.froggie_cloudy

        else -> -1
    }
    if (id != -1) {
        Image(
            painter = painterResource(id = id),
            contentDescription = getWeatherInterpretation(weatherCode),
            modifier = Modifier.fillMaxWidth().padding(top=40.dp)
        )
    }
}