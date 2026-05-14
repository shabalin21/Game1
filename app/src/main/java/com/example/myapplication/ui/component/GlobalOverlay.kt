package com.example.myapplication.ui.component

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.util.PerformanceMonitor
import com.example.myapplication.ui.theme.NeonGreen
import com.example.myapplication.ui.theme.NeonOrange
import com.example.myapplication.ui.theme.NeonPink
import kotlin.math.roundToInt

import com.example.myapplication.ui.debug.DevLabManager

@Composable
fun GlobalOverlay(
    performanceMonitor: PerformanceMonitor,
    devLabManager: DevLabManager? = null,
    showFps: Boolean = true
) {
    if (!showFps && devLabManager?.showFpsCounter != true) return

    val fps by performanceMonitor.fps
    val memory by performanceMonitor.memoryUsage
    val showMemory = devLabManager?.showMemoryUsage ?: false

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            val fpsColor = when {
                fps >= 55f -> NeonGreen
                fps >= 30f -> NeonOrange
                else -> NeonPink
            }
            
            Text(
                text = "${fps.roundToInt()} FPS",
                color = fpsColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            
            if (showMemory) {
                Text(
                    text = "${memory}MB RAM",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
