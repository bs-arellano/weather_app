package com.kogarashi.weather.ui.widgets
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(context: Context, coordinates: Pair<Double, Double>?){
    CenterAlignedTopAppBar(
        title = { coordinates?.let {
            Text(getCurrentCity(context, coordinates))
        }},
    )
}

@SuppressLint("MissingPermission")
@Composable
fun getCurrentCity(context: Context, coordinates: Pair<Double, Double>?): String {
    var city by remember { mutableStateOf("") }
    coordinates?.let { (latitude, longitude) ->
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses!!.isNotEmpty()) {
            city = addresses[0].locality ?: ""
        }
    }
    return city
}