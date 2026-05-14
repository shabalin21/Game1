package com.example.myapplication.ui.screen.casino.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.casino.model.*
import com.example.myapplication.ui.component.NeonButton
import com.example.myapplication.ui.theme.*
import java.util.Locale

@Composable
fun CrashView(
    state: CrashState,
    coins: Int,
    onStart: (Int) -> Unit,
    onCashOut: () -> Unit
) {
    var selectedWager by remember { mutableIntStateOf(100) }

    CasinoGameCard(accentColor = NeonPink) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GameSectionHeader("High Stakes", color = NeonPink)

            Spacer(modifier = Modifier.height(16.dp))

            // Multiplier Display
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.dp, NeonPink.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                CrashGraph(state = state)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val multiplierText = when (state) {
                        is CrashState.Rising -> String.format(Locale.US, "%.2f", state.multiplier)
                        is CrashState.Crashed -> String.format(Locale.US, "%.2f", state.crashPoint)
                        is CrashState.CashedOut -> String.format(Locale.US, "%.2f", state.cashOutMultiplier)
                        else -> "1.00"
                    }
                    val color = when (state) {
                        is CrashState.Rising -> if (state.multiplier > 5f) NeonRed else NeonCyan
                        is CrashState.Crashed -> NeonRed
                        is CrashState.CashedOut -> NeonGreen
                        else -> Color.Gray
                    }
                    
                    Text(
                        text = "${multiplierText}x",
                        color = color,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black
                    )
                    
                    if (state is CrashState.Crashed) {
                        Text("Crashed!", color = NeonRed, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black)
                    } else if (state is CrashState.CashedOut) {
                        Text("Payout: +${state.payout}", color = NeonGreen, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CONTROLS
            if (state !is CrashState.Rising) {
                BettingControls(
                    selectedWager = selectedWager,
                    onWagerChange = { selectedWager = it },
                    onAction = { onStart(selectedWager) },
                    actionText = "START",
                    enabled = coins >= selectedWager,
                    accentColor = NeonPink,
                    maxCoins = coins
                )
            } else {
                NeonButton(
                    text = "CASH OUT",
                    onClick = onCashOut,
                    color = NeonGreen,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
            }
        }
    }
}

@Composable
fun CrashGraph(state: CrashState) {
    val multiplier = when(state) {
        is CrashState.Rising -> state.multiplier
        is CrashState.Crashed -> state.crashPoint
        is CrashState.CashedOut -> state.cashOutMultiplier
        else -> 1f
    }

    val infiniteTransition = rememberInfiniteTransition(label = "GraphAnim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "Pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Grid
        repeat(5) { i ->
            val y = (i / 4f) * height
            drawLine(Color.White.copy(alpha = 0.05f), Offset(0f, y), Offset(width, y))
        }

        val progress = (multiplier / 10f).coerceAtMost(1f)
        val endX = progress * width
        val endY = height - progress * height
        
        val path = Path().apply {
            moveTo(0f, height)
            quadraticTo(width * 0.4f, height * 0.9f, endX, endY)
        }
        
        drawPath(
            path = path,
            color = if (state is CrashState.Crashed) NeonRed else NeonCyan.copy(alpha = pulse),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Point
        drawCircle(
            color = if (state is CrashState.Crashed) NeonRed else Color.White,
            radius = 4.dp.toPx(),
            center = Offset(endX, endY)
        )
    }
}
