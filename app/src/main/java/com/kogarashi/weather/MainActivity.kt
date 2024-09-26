package com.kogarashi.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.kogarashi.weather.data.model.HourlyForecast
import com.kogarashi.weather.data.model.WeatherResponse
import com.kogarashi.weather.data.repository.WeatherRepository
import com.kogarashi.weather.ui.theme.WeatherTheme
import com.kogarashi.weather.ui.widgets.CurrentWeatherWidget
import com.kogarashi.weather.ui.widgets.DailyForecastWidget
import com.kogarashi.weather.ui.widgets.HourlyForecastWidget
import com.kogarashi.weather.ui.widgets.MainTopBar
import com.kogarashi.weather.worker.WeatherWorker
import fetchCoordinates
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val weatherWorkRequest = PeriodicWorkRequestBuilder<WeatherWorker>(20, TimeUnit.MINUTES)
        .build()
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("WeatherCache", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleWeatherWorker(weatherWorkRequest)
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                Scaffold(
                    topBar = {
                        MainTopBar()
                    }
                ) { innerPadding->
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((innerPadding))
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        WeatherScreen(sharedPreferences, LocalContext.current)
                    }

                }
            }
        }
    }
    private fun scheduleWeatherWorker(workRequest: PeriodicWorkRequest) {
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}

@Composable
fun WeatherScreen(sharedPreferences: SharedPreferences, context: Context){
    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }
    var coordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var trigger by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val currentTime = System.currentTimeMillis()
    val lastUpdateTime = sharedPreferences.getLong("lastUpdateTime", 0)
    val cachedDataJson = sharedPreferences.getString("fetchedWeatherData", null)
    val gson = Gson()
    // Check if cached data is available and not expired
    if (currentTime - lastUpdateTime > 20 * 60 * 1000 || cachedDataJson == null) {
        isLoading = true
        Log.w("WeatherScreen", "No cache available or time has expired. Fetching weather data...")
        Log.v("WeatherScreen","Current cache:\n"+cachedDataJson.toString())
        // If data is not available or expired, fetch coordinates
        trigger = !trigger
    } else {
        weatherData = gson.fromJson(cachedDataJson, WeatherResponse::class.java)
        Log.d("WeatherScreen", "Using cached weather data.")
        Log.d("WeatherScreen", weatherData.toString())
        isLoading = false
    }
    // Fetch coordinates on trigger
    LaunchedEffect(trigger) {
        Log.d("WeatherScreen", "Fetching coordinates...")
        coordinates = fetchCoordinates(context)
        Log.d("WeatherScreen", "Fetched coordinates on if statement: $coordinates")
    }
    //Once coordinates are available, fetch weather data
    LaunchedEffect(coordinates) {
        Log.v("WeatherScreen","Coordinates: "+coordinates.toString())
        val repository = WeatherRepository(context)
        coordinates?.let { (latitude, longitude) ->
            repository.fetchWeatherData(
            latitude,
            longitude,
            onSuccess = { weatherResponse ->
                weatherData = weatherResponse
                Log.d("RESPONSE",weatherResponse.toString())
                repository.cacheWeatherData(weatherResponse, context)
                isLoading = false
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
            }
        )
        }
    }

    if (isLoading) {
        // Show a loading indicator or placeholder
        Text("Loading...")
    } else {
        // Display the weather data
        weatherData?.let { weatherResponse ->
            CurrentWeatherWidget(weatherResponse.currentWeather)
            HourlyForecastWidget(weatherResponse.hourlyForecast)
            DailyForecastWidget(weatherResponse.dailyForecast)
        }
    }
}

fun getHoursRange(hourlyForecast: HourlyForecast): Int {
    val currentHour = LocalDateTime.now()
    for (i in 0..<hourlyForecast.time.size) {
        val forecastHour = LocalDateTime.parse(hourlyForecast.time[i], DateTimeFormatter.ISO_DATE_TIME)
        if (!forecastHour.isBefore(currentHour)) {
            return i
        }
    }
    return 0
}

@SuppressLint("DefaultLocale")
fun convert24To12(time: String): String {
    val parts = time.split(":")
    if (parts.size != 2) {
        return "Invalid time format"
    }
    val hour = parts[0].toInt()
    if (hour < 0 || hour > 23) {
        return "Invalid hour"
    }
    val amPm = if (hour < 12) "am" else "pm"
    val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    return String.format("%d%s", hour12, amPm)
}