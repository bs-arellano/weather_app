package com.kogarashi.weather.domain

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kogarashi.weather.R

//Dictionary of WMO Weather interpretation codes
fun getWeatherInterpretation(weatherCode: Int): String {
    when(weatherCode){
        0 -> return "Clear sky"
        1 -> return "Mainly clear"
        2 -> return "Partly cloudy"
        3 -> return "Overcast"
        45 -> return "Fog"
        48 -> return "Depositing rime fog"
        51 -> return "Light drizzle"
        53 -> return "Moderate drizzle"
        55 -> return "Dense drizzle"
        56 -> return "Light freezing drizzle"
        57 -> return "Dense freezing drizzle"
        61 -> return "Slight rain"
        63 -> return "Moderate rain"
        65 -> return "Heavy rain"
        66 -> return "Light freezing rain"
        67 -> return "Heavy freezing rain"
        71 -> return "Slight snow fall"
        73 -> return "Moderate snow fall"
        75 -> return "Heavy snow fall"
        77 -> return "Snow grains"
        80 -> return "Slight rain showers"
        81 -> return "Moderate rain showers"
        82 -> return "Violent rain showers"
        85 -> return "Slight snow showers"
        86 -> return "Heavy snow showers"
        95 -> return "Thunderstorm"
        96 -> return "Thunderstorm with slight hail"
        99 -> return "Thunderstorm with heavy hail"
        else -> return "Unknown"
    }
}

//Dictionary of WMO Weather Images
@Composable
fun WeatherIcon(weatherCode: Int, size: Int = 100) {
    val id = getWeatherIcon(weatherCode)
    Image(
        painter = painterResource(id = id),
        contentDescription = getWeatherInterpretation(weatherCode),
        modifier = Modifier.size(size.dp)
    )
}

fun getWeatherIcon(weatherCode: Int): Int{
    val id: Int = when(weatherCode){
        0 -> R.drawable.clear_day
        1 -> R.drawable.mostly_clear_day
        2 -> R.drawable.mostly_cloudy_day
        3 -> R.drawable.cloudy
        45 -> R.drawable.haze_fog_dust_smoke
        48 -> R.drawable.haze_fog_dust_smoke
        51 -> R.drawable.drizzle
        53 -> R.drawable.drizzle
        55 -> R.drawable.drizzle
        56 -> R.drawable.drizzle
        57 -> R.drawable.drizzle
        61 -> R.drawable.scattered_showers_day
        63 -> R.drawable.scattered_showers_day
        65 -> R.drawable.heavy_rain
        66 -> R.drawable.heavy_rain
        67 -> R.drawable.heavy_rain
        80 -> R.drawable.scattered_showers_day
        81 -> R.drawable.scattered_showers_day
        82 -> R.drawable.scattered_showers_day
        95 -> R.drawable.isolated_thunderstorms
        96 -> R.drawable.isolated_thunderstorms
        99 -> R.drawable.isolated_thunderstorms


        else -> R.drawable.ban
    }
    return  id
}