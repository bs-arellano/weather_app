package com.kogarashi.weather.ui.widgets

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kogarashi.weather.R
import com.kogarashi.weather.data.model.HourlyForecast
import com.kogarashi.weather.domain.WeatherIcon
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HourlyForecastWidget(hourlyForecast: HourlyForecast){
    Card(
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp,15.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.clock),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Hourly Forecast icon",
                modifier = Modifier.padding(end = 10.dp)
            )
            Text("Hourly forecast", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
        val scrollState = rememberScrollState()
        Row (
            Modifier.horizontalScroll(scrollState).padding(horizontal = 20.dp).padding(bottom = 20.dp)
        ) {
            for (i in getHoursRange(hourlyForecast)..<getHoursRange(hourlyForecast) +24) {
                Column(
                    Modifier.padding(end = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(hourlyForecast.temperature[i].roundToInt().toString() + "°", fontWeight = FontWeight.SemiBold)
                    //Text(hourlyForecast.weatherCode[i].toString())
                    val fixedWeatherCode = hourlyForecast.weatherCode[i] + if (isHourDay(hourlyForecast.time[i].substring(11))) 0 else 100
                    WeatherIcon(fixedWeatherCode, size = 20)
                    if (hourlyForecast.precipitation[i] > 20) {
                        Text(((hourlyForecast.precipitation[i]/10f).toInt()*10).toString()  + "%", color = MaterialTheme.colorScheme.secondary)
                    }else{
                        Text("")
                    }
                    Text(convert24To12(hourlyForecast.time[i].substring(11)))
                }
            }
        }
    }
}

fun getHoursRange(hourlyForecast: HourlyForecast): Int {
    val currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
    for (i in 0..<hourlyForecast.time.size) {
        val forecastHour = LocalDateTime.parse(hourlyForecast.time[i], DateTimeFormatter.ISO_DATE_TIME)
        if (!forecastHour.isBefore(currentHour)) {
            return i
        }
    }
    return 0
}

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
    return String.format(Locale.getDefault(),"%d%s", hour12, amPm)
}

fun isHourDay(time: String): Boolean {
    val parts = time.split(":")
    if (parts.size != 2) {
        return false
    }
    val hour = parts[0].toInt()
    return hour in 6..17
}