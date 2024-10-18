package com.kogarashi.weather.ui.widgets


import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.kogarashi.weather.MainActivity
import com.kogarashi.weather.data.repository.WeatherRepository
import com.kogarashi.weather.data.repository.dataStore
import com.kogarashi.weather.domain.getWeatherIcon
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class CleanWidget: GlanceAppWidget() {

    private val currentDegrees = intPreferencesKey("currentDegrees")
    private val currentWeatherCode = intPreferencesKey("currentWeatherCode")
    private val isDay = intPreferencesKey("currentIsDay")

    suspend fun DataStore<Preferences>.loadWeather(context: Context) {
        Log.d("WeatherWidget", "Loading weather data from cache")
        val repository = WeatherRepository(context)
        updateData { prefs ->
            prefs.toMutablePreferences().apply {
                val cachedWeatherData = repository.getCachedWeatherData(context)
                this[currentDegrees] = cachedWeatherData?.currentWeather?.temperature?.toInt() ?: 0
                this[currentWeatherCode] = cachedWeatherData?.currentWeather?.weatherCode ?: -1
                this[isDay] = cachedWeatherData?.currentWeather?.isDay ?: 1
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        coroutineScope {
            val store = context.dataStore
            val currentDegrees = store.data
                .map { prefs -> prefs[currentDegrees] }
                .stateIn(this@coroutineScope)
            val currentWeatherCode = store.data
                .map { prefs -> prefs[currentWeatherCode] }
                .stateIn(this@coroutineScope)
            val currentIsDay = store.data
                .map { prefs -> prefs[isDay] }
                .stateIn(this@coroutineScope)
            // Load the current weather if there is not a current value present.
            if (currentDegrees.value == null || currentWeatherCode.value == null) store.loadWeather(context)
            // Create unique periodic work to keep this widget updated at a regular interval.
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "weatherWidgetWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequest.Builder(
                    WeatherWidgetWorker::class.java,
                    15.minutes.toJavaDuration()
                ).setInitialDelay(15.minutes.toJavaDuration()).build()
            )

            provideContent {
                val degrees by currentDegrees.collectAsState()
                val weatherCode by currentWeatherCode.collectAsState()
                val isDay by currentIsDay.collectAsState()
                val currentDay = LocalDateTime.now()
                val fixedWeatherCode = weatherCode?.let {
                    it + if (isDay==1) 0 else 100
                }?:-1
                val displayedDay = "${currentDay.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.substring(0, 3)}, ${currentDay.month.name.lowercase().replaceFirstChar { it.uppercase() }.substring(0, 3)} ${currentDay.dayOfMonth}"
                val scope = rememberCoroutineScope()
                val wallpaperColors: WallpaperColors? = WallpaperManager.getInstance(context).getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                var preferredTexColor = Color.White
                if (wallpaperColors != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    wallpaperColors.let { colors ->
                        // Check if the wallpaper supports dark text
                        val darkTextSupported = colors.colorHints and WallpaperColors.HINT_SUPPORTS_DARK_TEXT != 0

                        preferredTexColor = if (darkTextSupported) {
                            Color.Black
                        } else {
                            Color.White
                        }
                    }
                }
                Column(
                    modifier = GlanceModifier.fillMaxSize().padding(vertical = 5.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.Horizontal.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(displayedDay,style = androidx.glance.text.TextStyle(
                        fontSize = 22.sp,
                        fontWeight = androidx.glance.text.FontWeight.Normal,
                        color = androidx.glance.color.ColorProvider(day=preferredTexColor, night = preferredTexColor),
                    ),
                        modifier = GlanceModifier.clickable {
                            scope.launch {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.calendar")
                                Log.v("WeatherWidgetWorker", "Launch Intent: $launchIntent")
                                if (launchIntent != null) {
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launchIntent)
                                } else {
                                    val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.calendar"))
                                    playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(playStoreIntent)
                                }
                            }
                        }
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.padding(top = 5.dp)
                            .clickable {
                                scope.launch {
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                    context.dataStore.loadWeather(context)
                                }
                            },
                    )
                    {
                        Image(
                            provider = androidx.glance.ImageProvider(
                                getWeatherIcon(fixedWeatherCode)
                            ),
                            contentDescription = "Weather Icon",
                            modifier = GlanceModifier.size(25.dp).padding(end = 10.dp)
                        )
                        Text("$degrees°C", style = androidx.glance.text.TextStyle(
                            fontSize = 18.sp,
                            fontWeight = androidx.glance.text.FontWeight.Normal,
                            color = androidx.glance.color.ColorProvider(day=preferredTexColor, night = preferredTexColor),
                        ))
                    }
                }

            }
        }
    }
}

class WeatherWidgetWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        Log.d("WeatherWidgetWorker", "doWork() called")
        CleanWidget().apply {
            applicationContext.dataStore.loadWeather(applicationContext)
            // Call update/updateAll in case a Worker for the widget is not currently running.
            updateAll(applicationContext)
        }
        return Result.success()
    }
}