package com.example.myapplication.ui.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.NeonBlue
import com.example.myapplication.util.PerformanceMonitor

@Composable
fun PerformanceOverlay(
    monitor: PerformanceMonitor,
    manager: DevLabManager,
    modifier: Modifier = Modifier
) {
    if (!manager.showPerformanceOverlay) return

    val fps by monitor.fps
    val frameTime by monitor.frameTime
    val memory by monitor.memoryUsage

    val color = when {
        fps >= 90f -> Color(0xFF00E676) // Green
        fps >= 50f -> Color(0xFFFFD600) // Yellow
        else -> Color(0xFFFF1744)       // Red
    }

    Box(
        modifier = modifier
            .padding(8.dp)
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${fps.toInt()} FPS",
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = String.format("%.1fms", frameTime),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            if (manager.showMemoryUsage) {
                Text(
                    text = "${memory}MB MEM",
                    color = NeonBlue,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
