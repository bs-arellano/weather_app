package com.kogarashi.weather.data.model

data class WeatherData (
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val currentWeather: CurrentWeather,
    val hourlyForecast: HourlyForecast,
    val dailyForecast: DailyForecast
)