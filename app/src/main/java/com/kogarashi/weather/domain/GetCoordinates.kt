import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun fetchCoordinates(context: Context): Pair<Double, Double>? {
    return suspendCoroutine { continuation ->
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1
                )
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    continuation.resume(Pair(it.latitude, it.longitude))
                } ?: continuation.resume(null)
            }.addOnFailureListener {
                it.printStackTrace()
                continuation.resume(null) // Handle failure
            }
        } else {
            continuation.resume(null) // GPS not enabled
        }
    }
}

@Composable
fun getCoordinates(): Pair<Double, Double>? {
    var coordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coordinates = fetchCoordinates(context)
    }

    return coordinates
}