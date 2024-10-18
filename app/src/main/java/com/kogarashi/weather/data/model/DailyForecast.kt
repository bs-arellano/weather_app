package com.kogarashi.weather.data.model

import com.google.gson.annotations.SerializedName

data class DailyForecast(
    @SerializedName("time") val time: ArrayList<String>,
    @SerializedName("weather_code") val weatherCode: ArrayList<Int>,
    @SerializedName("temperature_2m_max") val maxTemperature: ArrayList<Float>,
    @SerializedName("temperature_2m_min") val minTemperature: ArrayList<Float>,
    @SerializedName("precipitation_probability_max") val precipitation: ArrayList<Int>,
    @SerializedName("uv_index_max") val uvIndex: ArrayList<Float>
)