package com.kogarashi.weather.domain

import android.content.Context
import android.util.Log
import com.kogarashi.weather.data.model.WeatherData
import com.kogarashi.weather.data.repository.WeatherRepository

fun fetchWeatherData(context: Context, coordinates: Pair<Double, Double>, onWeatherDataFetched: (WeatherData) -> Unit){
    Log.d("fetchWeatherData", "Fetching weather data for coordinates: $coordinates")
    val repository = WeatherRepository(context)
    val (latitude, longitude) = coordinates
    val currentTime = System.currentTimeMillis()
    val lastUpdateTime = repository.getLastUpdateTime(context)
    val cacheValidity = 10 * 60 * 1000 // 10 minutes in milliseconds
    if (currentTime - lastUpdateTime < cacheValidity) {
        Log.d("fetchWeatherData", "Using cached weather data")
        val cachedWeatherData = repository.getCachedWeatherData(context)
        if (cachedWeatherData != null) {
            onWeatherDataFetched(cachedWeatherData)
        }
    } else {
        Log.d("fetchWeatherData", "No valid cache found, fetching weather data from API")
        repository.fetchWeatherData(
            latitude,
            longitude,
            onSuccess = { weatherData ->
                Log.v("fetchWeatherData", "Weather Data Fetched: $weatherData")
                repository.cacheWeatherData(weatherData, context)
                onWeatherDataFetched(weatherData)
            },
            onError = { error ->
                val maxLogSize = 1000
                val errorMessageLength = error.message?.length ?: 0
                for (i in 0..errorMessageLength / maxLogSize) {
                    val start = i * maxLogSize
                    var end = (i + 1) * maxLogSize
                    end = if (end > errorMessageLength) errorMessageLength else end
                    error.message?.let { Log.e("WeatherScreen", it.substring(start, end)) }
                }
                android.os.Handler().postDelayed({
                    fetchWeatherData(context, coordinates, onWeatherDataFetched)
                }, 500)
            }
        )
    }
}