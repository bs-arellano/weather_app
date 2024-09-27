package com.kogarashi.weather.data.repository

import android.content.Context
import com.google.gson.Gson
import com.kogarashi.weather.data.api.WeatherApi
import com.kogarashi.weather.data.model.WeatherData

class WeatherRepository(context: Context) {

    private val api = WeatherApi(context)

    fun fetchWeatherData(latitude: Double, longitude: Double, onSuccess: (WeatherData) -> Unit, onError: (Exception) -> Unit) {
        api.getWeatherData(latitude, longitude, onSuccess, onError)
    }
    fun cacheWeatherData(weatherData: WeatherData, context: Context) {
        val sharedPreferences = context.getSharedPreferences("WeatherCache", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        editor.putString("fetchedWeatherData", gson.toJson(weatherData))
        editor.putLong("lastUpdateTime", System.currentTimeMillis())  // Save the timestamp
        editor.apply()
    }
}