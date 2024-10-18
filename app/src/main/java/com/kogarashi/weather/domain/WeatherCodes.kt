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
        3,103 -> R.drawable.cloudy
        45,48,145,148 -> R.drawable.haze_fog_dust_smoke
        51,53,55,56,57,151,153,155,156,157 -> R.drawable.drizzle
        61,63,80,81,82 -> R.drawable.scattered_showers_day
        65,66,67,165,166,167 -> R.drawable.heavy_rain
        95,96,99,195,196,199 -> R.drawable.isolated_thunderstorms
        100 -> R.drawable.clear_night
        101 -> R.drawable.mostly_clear_night
        102 -> R.drawable.mostly_cloudy_night
        161,163,180,181,182 -> R.drawable.scattered_showers_night


        else -> R.drawable.ban
    }
    return  id
}