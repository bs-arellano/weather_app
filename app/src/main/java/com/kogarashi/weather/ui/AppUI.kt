package com.kogarashi.weather.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kogarashi.weather.data.model.WeatherData
import com.kogarashi.weather.ui.theme.WeatherTheme
import com.kogarashi.weather.ui.widgets.CurrentWeatherWidget
import com.kogarashi.weather.ui.widgets.DailyForecastWidget
import com.kogarashi.weather.ui.widgets.FroggieImage
import com.kogarashi.weather.ui.widgets.HourlyForecastWidget
import com.kogarashi.weather.ui.widgets.MainTopBar
import com.kogarashi.weather.ui.widgets.RelativeHumidityWidget
import com.kogarashi.weather.ui.widgets.UVIndexWidget

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
        Scaffold(topBar = { MainTopBar(LocalContext.current, coordinates) }, ) { innerPadding ->
            val scrollState = rememberScrollState()
            val modifiedPadding = PaddingValues(top = innerPadding.calculateTopPadding(), bottom = 0.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(modifiedPadding)
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
    WeatherConditions(weatherData)
    FroggieImage(weatherData.currentWeather.weatherCode + if (weatherData.currentWeather.isDay==1) 0 else 100)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeatherConditions(weatherData: WeatherData){
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Adjust spacing as needed
        verticalArrangement = Arrangement.spacedBy(8.dp) // Adjust spacing as needed
    ){
        RelativeHumidityWidget(weatherData.currentWeather)
        UVIndexWidget(weatherData.dailyForecast.uvIndex[0].toInt())
    }
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