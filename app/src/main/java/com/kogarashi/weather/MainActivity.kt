package com.kogarashi.weather

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kogarashi.weather.data.model.WeatherData
import com.kogarashi.weather.domain.PermissionHandler
import com.kogarashi.weather.domain.fetchCoordinates
import com.kogarashi.weather.domain.fetchWeatherData
import com.kogarashi.weather.ui.WeatherAppUI
import com.kogarashi.weather.worker.WeatherWorker
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    private lateinit var permissionHandler: PermissionHandler
    private val weatherWorkRequest = PeriodicWorkRequestBuilder<WeatherWorker>(1, TimeUnit.HOURS)
        .build()
    // Set up state to manage which UI to show
    private val coordinatesState = mutableStateOf<Pair<Double, Double>?>(null)
    private val weatherDataState = mutableStateOf<WeatherData?>(null)
    private val permissionGranted = mutableStateOf(false)  // Whether permission is granted
    private val weatherDataFetched = mutableStateOf(false)  // Whether weather data is fetched
    private val permissionDenied = mutableStateOf(false)  // Whether permission is denied
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        permissionHandler = PermissionHandler(this, this)

        // Set up the callback to handle permission result (whether granted or denied)
        permissionHandler.initialize { isGranted ->
            if (isGranted) {
                permissionGranted.value = true
                fetchWeatherDataIfPermissionGranted(coordinatesState, weatherDataState)
            } else {
                permissionDenied.value = true
            }
        }

        // Check if permission is already granted
        if (permissionHandler.isLocationPermissionGranted()) {
            permissionGranted.value = true
            WorkManager.getInstance(this).enqueueUniquePeriodicWork("weatherWork", ExistingPeriodicWorkPolicy.KEEP, weatherWorkRequest)
            Log.d("MainActivity", "Worker enqueued")
            fetchWeatherDataIfPermissionGranted(coordinatesState, weatherDataState)
        } else {
            // Permission not granted, so request it
            permissionHandler.requestLocationPermission()
        }

        setContent {
            WeatherAppUI(
                permissionGranted = permissionGranted.value,
                weatherDataFetched = weatherDataFetched.value,
                permissionDenied = permissionDenied.value,
                weatherData = weatherDataState.value,
                coordinates = coordinatesState.value,
                onRequestPermissionAgain = {
                    permissionHandler.requestLocationPermission()
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (weatherDataState.value!=null && coordinatesState.value!=null){
            fetchWeatherDataIfPermissionGranted(coordinatesState, weatherDataState)
        }
    }
    private fun fetchWeatherDataIfPermissionGranted(
        coordinatesState: MutableState<Pair<Double, Double>?>,
        weatherDataState: MutableState<WeatherData?>
    ) {
        fetchCoordinates (this) { coordinates ->
            coordinatesState.value = coordinates
            // Once coordinates are fetched, fetch weather data
            fetchWeatherData(this, coordinates) { weatherData ->
                weatherDataState.value = weatherData
            }
        }
    }
}

