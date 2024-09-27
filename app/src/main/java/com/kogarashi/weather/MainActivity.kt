package com.kogarashi.weather

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kogarashi.weather.data.model.WeatherData
import com.kogarashi.weather.domain.PermissionHandler
import com.kogarashi.weather.domain.fetchCoordinates
import com.kogarashi.weather.domain.fetchWeatherData
import com.kogarashi.weather.ui.theme.WeatherTheme
import com.kogarashi.weather.ui.widgets.CurrentWeatherWidget
import com.kogarashi.weather.ui.widgets.DailyForecastWidget
import com.kogarashi.weather.ui.widgets.HourlyForecastWidget
import com.kogarashi.weather.ui.widgets.MainTopBar


class MainActivity : ComponentActivity() {
    private lateinit var permissionHandler: PermissionHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Set up state to manage which UI to show
        val coordinatesState = mutableStateOf<Pair<Double, Double>?>(null)
        val weatherDataState = mutableStateOf<WeatherData?>(null)
        val permissionGranted = mutableStateOf(false)  // Whether permission is granted
        val weatherDataFetched = mutableStateOf(false)  // Whether weather data is fetched
        val permissionDenied = mutableStateOf(false)  // Whether permission is denied

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

@Composable
fun WeatherAppUI(
    weatherData: WeatherData?,
    permissionGranted: Boolean,
    coordinates: Pair<Double, Double>?,
    permissionDenied: Boolean,
    weatherDataFetched: Boolean,
    onRequestPermissionAgain: () -> Unit
) {
    Log.v("WeatherAppUI", "Weather Data: ${weatherData.toString()}")
    Log.v("WeatherAppUI", "Permission Granted: $permissionGranted")
    Log.v("WeatherAppUI", "Coordinates: $coordinates")
    Log.v("WeatherAppUI", "Permission Denied: $permissionDenied")
    Log.v("WeatherAppUI", "Weather Data Fetched: $weatherDataFetched")
    WeatherTheme {
        Scaffold(topBar = { MainTopBar(LocalContext.current, coordinates)}, ) { innerPadding ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((innerPadding))
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                when {
                    weatherData != null -> {
                        // If weather data is fetched, show WeatherDataUI
                        WeatherScreen(weatherData)
                    }
                    permissionDenied -> {
                        // If permission is denied, show Permission Denied UI
                        PermissionDeniedUI(onRequestPermissionAgain)
                    }
                    else -> {
                        // Show a loading or waiting state
                        LoadingUI()
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(weatherData: WeatherData){
    CurrentWeatherWidget(weatherData.currentWeather)
    HourlyForecastWidget(weatherData.hourlyForecast)
    DailyForecastWidget(weatherData.dailyForecast)
}

@Composable
fun PermissionDeniedUI(onRequestPermissionAgain: () -> Unit) {
    // UI to show when the permission is denied
    Column {
        Text("Location permission is required to fetch weather data.")
        Button(onClick = onRequestPermissionAgain) {
            Text("Request Permission Again")
        }
    }
}

@Composable
fun LoadingUI() {
    // UI shown while waiting for permission or data fetching
    Text("Loading...")
}