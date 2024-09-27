package com.kogarashi.weather.ui.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kogarashi.weather.data.model.CurrentWeather
import com.kogarashi.weather.domain.WeatherIcon
import com.kogarashi.weather.domain.getWeatherInterpretation

@Composable
fun CurrentWeatherWidget(currentWeather: CurrentWeather){
    Text(getWeatherInterpretation(currentWeather.weatherCode), fontSize = 20.sp, modifier = Modifier.padding(top = 30.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ){
        Text(currentWeather.temperature.toInt().toString(), fontSize = 150.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp))
        WeatherIcon(currentWeather.weatherCode, 70)
    }
    Text("Feels like ${currentWeather.apparentTemperature.toInt()}°C")
}