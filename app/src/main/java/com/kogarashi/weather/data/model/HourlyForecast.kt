package com.kogarashi.weather.data.model

import com.google.gson.annotations.SerializedName

data class HourlyForecast (
    @SerializedName("time") val time: ArrayList<String>,
    @SerializedName("temperature_2m")val temperature: ArrayList<Float>,
    @SerializedName("weather_code") val weatherCode: ArrayList<Int>,
    @SerializedName("precipitation_probability") val precipitation: ArrayList<Int>,
)