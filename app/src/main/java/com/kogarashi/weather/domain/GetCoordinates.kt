package com.kogarashi.weather.domain

import android.annotation.SuppressLint
import android.app.Activity
import com.google.android.gms.location.LocationServices

// Function to fetch the user's coordinates
@SuppressLint("MissingPermission") // Ensure permissions are checked before calling this function
fun fetchCoordinates(activity: Activity, onCoordinatesFetched: (Pair<Double, Double>) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            // Once coordinates are fetched, pass them to the callback
            val coordinates = Pair(it.latitude, it.longitude)
            onCoordinatesFetched(coordinates)
        }
    }.addOnFailureListener {
        // Handle failure case, like showing an error message
    }
}