package com.example.myapplication.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.myapplication.domain.model.TimeOfDay

@Composable
fun EnvironmentOverlay(
    timeOfDay: TimeOfDay,
    modifier: Modifier = Modifier
) {
    val topColor = when (timeOfDay) {
        TimeOfDay.DAWN -> Color(0xFFFFADAD)
        TimeOfDay.DAY -> Color(0xFF87CEEB)
        TimeOfDay.DUSK -> Color(0xFFFF9E80)
        TimeOfDay.NIGHT -> Color(0xFF1A237E)
    }

    val bottomColor = when (timeOfDay) {
        TimeOfDay.DAWN -> Color(0xFFFFD6A5)
        TimeOfDay.DAY -> Color(0xFFE0F7FA)
        TimeOfDay.DUSK -> Color(0xFF455A64)
        TimeOfDay.NIGHT -> Color(0xFF000000)
    }

    val animatedTopColor by animateColorAsState(
        targetValue = topColor.copy(alpha = 0.15f), // Subtler atmospheric overlay
        animationSpec = tween(durationMillis = 5000),
        label = "AtmosphereTop"
    )

    val animatedBottomColor by animateColorAsState(
        targetValue = bottomColor.copy(alpha = 0.15f),
        animationSpec = tween(durationMillis = 5000),
        label = "AtmosphereBottom"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(animatedTopColor, animatedBottomColor)
                )
            )
    )
}
