package com.kogarashi.weather.data.model

import com.google.gson.annotations.SerializedName

data class CurrentWeather (
    @SerializedName("temperature_2m") val temperature: Float,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("apparent_temperature") val apparentTemperature: Float,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("is_day") val isDay: Int,
)