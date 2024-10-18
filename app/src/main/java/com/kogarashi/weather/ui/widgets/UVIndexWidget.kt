package com.kogarashi.weather.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kogarashi.weather.R
import kotlin.math.sin

@Composable
fun UVIndexWidget(uvIndex: Int){
    Card(
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .padding(10.dp, 15.dp)
            .width(180.dp)
            .height(150.dp)
    ){
        val strokeColor = Color(0xff7700ff).copy(alpha = 0.4f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                val filledPercentage = uvIndex / 11f
                val wavePath = Path()
                    .apply {
                        moveTo(0f, size.height) // Start at the bottom left
                        // Adjust these values to control the wave shape
                        val waveAmplitude = 5f
                        val waveFrequency = 0.1f
                        for (x in 0..(size.width*1.2).toInt() step 2) {
                            val y =
                                (size.height*(1-filledPercentage)) - waveAmplitude * sin(x * waveFrequency)
                            lineTo(x.toFloat(), y)
                        }
                        lineTo(size.width, size.height) // Connect to the bottom right
                        close() // Close the path
                    }
                onDrawBehind {
                    // Draw the wave path as the top border
                    drawPath(
                        wavePath,
                        color = strokeColor,

                        )
                }
            },
            contentAlignment = Alignment.TopStart
        ){
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Icon(
                    painter = painterResource(id = R.drawable.sun),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "UV index icon",
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text(text = "UV Index", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Column(
                modifier = Modifier
                    .padding(20.dp, 0.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = uvIndex.toString(),
                    fontSize = 50.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

    }
}