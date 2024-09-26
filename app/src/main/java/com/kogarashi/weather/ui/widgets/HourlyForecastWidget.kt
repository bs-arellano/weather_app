package com.kogarashi.weather.ui.widgets

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kogarashi.weather.convert24To12
import com.kogarashi.weather.data.model.HourlyForecast
import com.kogarashi.weather.domain.WeatherIcon
import com.kogarashi.weather.getHoursRange
import kotlin.math.roundToInt

@Composable
fun HourlyForecastWidget(hourlyForecast: HourlyForecast){
    Card(
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 20.dp)
    ) {
        Text("Hourly forecast", modifier = Modifier.padding(20.dp), fontSize = 15.sp)
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
                    WeatherIcon(hourlyForecast.weatherCode[i], size = 20)
                    if (hourlyForecast.precipitation[i] > 20) {
                        Text(hourlyForecast. precipitation[ i] . toString( )  + "%")
                    }else{
                        Text("")
                    }
                    Text(convert24To12(hourlyForecast.time[i].substring(11)))
                }
            }
        }
    }
}