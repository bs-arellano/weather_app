package com.kogarashi.weather.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kogarashi.weather.data.repository.WeatherRepository
import fetchCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams)
{
    override suspend fun doWork(): Result {
        Log.d("WeatherWorker", "doWork() called")
        return withContext(Dispatchers.IO) {
            try {
                val repository = WeatherRepository(context)
                val coordinates = fetchCoordinates(context)
                coordinates?.let { (latitude, longitude) ->
                    Log.d("WeatherWorker", "Fetching weather data for coordinates: $latitude, $longitude")
                    repository.fetchWeatherData(
                        latitude,
                        longitude,
                        onSuccess = { weatherResponse ->
                            repository.cacheWeatherData(weatherResponse, context)
                        },
                        onError = { error ->
                            Log.e("WeatherWorker", "Error fetching weather data: ${error.message}")
                            // Handle errors if necessary
                        }
                    )
                    Result.success()
                } ?: Result.failure()  // Return failure if coordinates are null
            } catch (e: Exception) {
                Result.retry()  // Retry in case of failure
            }
        }
    }
}