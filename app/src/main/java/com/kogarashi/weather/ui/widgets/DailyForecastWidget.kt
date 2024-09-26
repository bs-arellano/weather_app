package com.kogarashi.weather.ui.widgets

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kogarashi.weather.data.model.DailyForecast
import com.kogarashi.weather.domain.WeatherIcon
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@SuppressLint("DefaultLocale")
@Composable
fun DailyForecastWidget(dailyForecast: DailyForecast){
    Card(
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 20.dp)
    ) {
        Text("Daily forecast", modifier = Modifier.padding(20.dp), fontSize = 15.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column {
                for (i in 0..<dailyForecast.time.size) {
                    val dateRepresentation = getDateRepresentation(dailyForecast.time[i])
                    Text(dateRepresentation, modifier = Modifier.padding(bottom = 10.dp))
                }
            }
            Column (
                horizontalAlignment = Alignment.End
            ) {
                for (i in 0..<dailyForecast.weatherCode.size) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.padding(bottom = 10.dp)
                    ){
                        if (dailyForecast.precipitation[i] > 20) {
                            Text(dailyForecast. precipitation[ i] . toString( )  + "%")
                        }
                        //Text(String.format("%02d", dailyForecast.weatherCode[i]))
                        WeatherIcon(dailyForecast.weatherCode[i], size = 20)
                    }
                }
            }
            Column (
                horizontalAlignment = Alignment.End
            ) {
                for (i in 0..<dailyForecast.maxTemperature.size) {
                    Text(dailyForecast.maxTemperature[i].roundToInt().toString() + "°/"+ dailyForecast.minTemperature[i].roundToInt().toString() + "°", modifier = Modifier.padding(bottom = 10.dp))
                }
            }
        }
    }
}

fun getDateRepresentation(date: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateTime = LocalDate.parse(date, formatter).atStartOfDay()
    val dayOfWeek = dateTime.dayOfWeek.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val dayOfMonth = dateTime.dayOfMonth
    val month = dateTime.month.name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    return "$dayOfWeek, $dayOfMonth ${month.substring(0,3)}"
}
