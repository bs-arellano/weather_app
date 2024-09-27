package com.kogarashi.weather.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.kogarashi.weather.data.api.WeatherApi
import com.kogarashi.weather.data.model.WeatherData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WeatherCache")

class WeatherRepository(context: Context) {

    private val api = WeatherApi(context)


    fun fetchWeatherData(latitude: Double, longitude: Double, onSuccess: (WeatherData) -> Unit, onError: (Exception) -> Unit) {
        api.getWeatherData(latitude, longitude, onSuccess, onError)
    }

    fun cacheWeatherData(weatherData: WeatherData, context: Context) {
        val gson = Gson()
        val weatherDataJson = gson.toJson(weatherData)

        // Define keys
        val weatherDataKey = stringPreferencesKey("fetchedWeatherData")
        val lastUpdateTimeKey = longPreferencesKey("lastUpdateTime")

        // Save data to DataStore
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences[weatherDataKey] = weatherDataJson
                preferences[lastUpdateTimeKey] = System.currentTimeMillis()
            }
        }
    }

    fun getCachedWeatherData(context: Context): WeatherData? {
        val gson = Gson()
        val weatherDataKey = stringPreferencesKey("fetchedWeatherData")

        return runBlocking {
            val weatherDataJson = context.dataStore.data
                .map { preferences ->
                    preferences[weatherDataKey]
                }.first()

            if (weatherDataJson != null) {
                gson.fromJson(weatherDataJson, WeatherData::class.java)
            } else {
                null
            }
        }
    }

    fun getLastUpdateTime(context: Context): Long {
        val lastUpdateTimeKey = longPreferencesKey("lastUpdateTime")
        return runBlocking {
            context.dataStore.data
                .map { preferences ->
                    preferences[lastUpdateTimeKey] ?: 0L
                }.first()
        }
    }
}