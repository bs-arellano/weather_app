package com.kogarashi.weather.ui.widgets

import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import getCoordinates
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(){
    CenterAlignedTopAppBar(
        title = { Text(getCurrentCity()) },
    )
}

@SuppressLint("MissingPermission")
@Composable
fun getCurrentCity(): String {
    var city by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coordinates = getCoordinates()

    coordinates?.let { (latitude, longitude) ->
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses!!.isNotEmpty()) {
            city = addresses[0].locality ?: ""
        }
    }

    return city
}