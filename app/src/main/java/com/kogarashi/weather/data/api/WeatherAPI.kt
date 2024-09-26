package com.kogarashi.weather.data.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kogarashi.weather.data.model.CurrentWeather
import com.kogarashi.weather.data.model.DailyForecast
import com.kogarashi.weather.data.model.HourlyForecast
import com.kogarashi.weather.data.model.WeatherResponse


class WeatherApi(private val context: Context) {

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun getWeatherData(latitude: Double, longitude: Double, onSuccess: (WeatherResponse) -> Unit, onError: (Exception) -> Unit) {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code&hourly=temperature_2m,precipitation_probability,weather_code&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=auto"
        Log.d("API", "Fetching data from $url")
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val latitude = response.getDouble("latitude")
                    val longitude = response.getDouble("longitude")
                    val timezone = response.getString("timezone")
                    val current = response.getJSONObject("current")
                    val hourly = response.getJSONObject("hourly")
                    val daily = response.getJSONObject("daily")


                    val currentWeather = Gson().fromJson(current.toString(), CurrentWeather::class.java)
                    val hourlyForecast = Gson().fromJson(hourly.toString(), HourlyForecast::class.java)
                    val dailyForecast = Gson().fromJson(daily.toString(), DailyForecast::class.java)


                    val weatherResponse = WeatherResponse(
                        latitude, longitude, timezone,currentWeather, hourlyForecast, dailyForecast
                    )
                    onSuccess(weatherResponse)
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error ->
                onError(error)
            }
        )
        requestQueue.add(request)
    }
}