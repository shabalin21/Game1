package com.example.myapplication.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.*

@Composable
fun BTCChartComponent(
    history: List<Float>,
    modifier: Modifier = Modifier,
    accentColor: Color = PremiumGold
) {
    if (history.size < 2) return

    val minPrice = history.minOrNull() ?: 0f
    val maxPrice = history.maxOrNull() ?: 1f
    val range = (maxPrice - minPrice).coerceAtLeast(1f)

    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val points = history.size
        val dx = width / (points - 1)

        val path = Path()
        val fillPath = Path()

        history.forEachIndexed { index, price ->
            val x = index * dx
            val y = height - ((price - minPrice) / range * height)
            
            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
            
            if (index == history.size - 1) {
                fillPath.lineTo(x, height)
                fillPath.close()
            }
        }

        // Draw Glow
        drawPath(
            path = path,
            color = accentColor.copy(alpha = glowAlpha * 0.2f),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw Line
        drawPath(
            path = path,
            color = accentColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw Fill Gradient
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(accentColor.copy(alpha = 0.2f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )
    }
}

