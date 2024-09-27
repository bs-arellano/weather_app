package com.kogarashi.weather.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kogarashi.weather.data.model.WeatherData
import com.kogarashi.weather.data.repository.WeatherRepository
import com.kogarashi.weather.domain.fetchCoordinates
import com.kogarashi.weather.domain.fetchWeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WeatherWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams)
{
    override suspend fun doWork(): Result {
        val repository = WeatherRepository(context)
        Log.d("WeatherWorker", "doWork() called")
        return withContext(Dispatchers.IO) {
            try {
                val coordinates = suspendCoroutine<Pair<Double, Double>> { continuation ->
                    fetchCoordinates(context) { coordinates ->
                        Log.d("WeatherWorker", "Coordinates fetched: $coordinates")
                        continuation.resume(coordinates)
                    }
                }
                val weatherData = suspendCoroutine<WeatherData> { continuation ->
                    fetchWeatherData(context, coordinates) { weatherData ->
                        Log.d("WeatherWorker", "Weather data fetched: $weatherData")
                        continuation.resume(weatherData)
                    }
                }

                // Cache weather data
                repository.cacheWeatherData(weatherData, context)
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is com.android.volley.TimeoutError) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
    }
}