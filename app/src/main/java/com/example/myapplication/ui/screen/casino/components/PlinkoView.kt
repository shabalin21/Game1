package com.example.myapplication.ui.screen.casino.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.domain.casino.model.*
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*
import kotlin.math.sqrt

@Composable
fun PlinkoView(
    state: PlinkoState,
    coins: Int,
    onDrop: (Int) -> Unit,
    onRiskChange: (PlinkoRisk) -> Unit
) {
    var selectedWager by remember { mutableIntStateOf(10) }
    val accentColor = getRiskColor(state.activeRisk)

    CasinoGameCard(accentColor = accentColor) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Risk Selection Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlinkoRisk.entries.forEach { risk ->
                    val isSelected = state.activeRisk == risk
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) getRiskColor(risk).copy(alpha = 0.2f) else SurfaceDark)
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) getRiskColor(risk) else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onRiskChange(risk) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = risk.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) getRiskColor(risk) else Color.White.copy(alpha = 0.4f),
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                        )
                    }
                }
            }

            // MAIN BOARD AREA
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                PlinkoBoard(state, accentColor)
            }

            // CONTROLS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Multiplier History
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    state.recentPayouts.take(8).forEach { payout ->
                        Text(
                            text = "${payout}x",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (payout >= 2f) NeonGreen else if (payout >= 1f) NeonYellow else NeonRed,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // BETTING CONTROLS
                BettingControls(
                    selectedWager = selectedWager,
                    onWagerChange = { selectedWager = it },
                    onAction = { onDrop(selectedWager) },
                    actionText = "DROP BALL",
                    enabled = coins >= selectedWager,
                    accentColor = accentColor,
                    maxCoins = coins
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PlinkoPreview() {
    PetSimulationTheme {
        PlinkoView(
            state = PlinkoState(
                balls = listOf(PlinkoBall(1, 0f, 5f)),
                activeRisk = PlinkoRisk.MEDIUM,
                recentPayouts = listOf(1.5f, 0.5f, 5.0f)
            ),
            coins = 1000,
            onDrop = {},
            onRiskChange = {}
        )
    }
}

@Composable
fun PlinkoBoard(state: PlinkoState, accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "BoardGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val rowCount = 10
        val pegRadius = 3.dp.toPx()
        val ballRadius = 5.dp.toPx()

        // Scanlines / Background Grid
        for (i in 0..10) {
            val y = (i / 10f) * h
            drawLine(
                color = accentColor.copy(alpha = 0.05f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw Pegs
        for (row in 0 until rowCount) {
            val y = (row + 1) * (h / (rowCount + 2))
            val pegsInRow = row + 3
            val startX = w / 2 - (pegsInRow - 1) * (w / (rowCount + 4)) * 0.5f
            val spacing = w / (rowCount + 4)
            
            for (i in 0 until pegsInRow) {
                val x = startX + i * spacing
                
                // Check if any ball is near this peg for hit effect
                val isHit = state.balls.any { 
                    val dx = (it.x * spacing + w/2) - x
                    val dy = ((it.y + 1) * (h / (rowCount + 2))) - y
                    sqrt(dx*dx + dy*dy) < 15.dp.toPx()
                }

                drawCircle(
                    color = if (isHit) Color.White else accentColor.copy(alpha = pulse),
                    radius = if (isHit) pegRadius * 1.5f else pegRadius,
                    center = Offset(x, y)
                )
            }
        }

        // Draw Multiplier Slots at bottom
        val slotY = (rowCount + 1) * (h / (rowCount + 2))
        val slotsCount = 11
        val slotWidth = w / slotsCount
        
        for (i in 0 until slotsCount) {
            val x = i * slotWidth
            drawRect(
                color = accentColor.copy(alpha = 0.1f),
                topLeft = Offset(x + 2.dp.toPx(), slotY),
                size = androidx.compose.ui.geometry.Size(slotWidth - 4.dp.toPx(), 20.dp.toPx())
            )
        }

        // Draw Balls
        state.balls.forEach { ball ->
            // COORDINATE MAPPING: Domain logic uses row index as Y and relative offset as X
            // X center is 0 in domain.
            val spacing = w / (rowCount + 4)
            val screenX = w/2 + (ball.x * spacing)
            val screenY = (ball.y + 1) * (h / (rowCount + 2))
            
            // Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, accentColor, Color.Transparent),
                    center = Offset(screenX, screenY),
                    radius = ballRadius * 3
                ),
                radius = ballRadius * 3,
                center = Offset(screenX, screenY)
            )
            
            drawCircle(
                color = Color.White,
                radius = ballRadius,
                center = Offset(screenX, screenY)
            )
        }
    }
}

fun getRiskColor(risk: PlinkoRisk) = when(risk) {
    PlinkoRisk.LOW -> CyberGreen
    PlinkoRisk.MEDIUM -> CyberYellow
    PlinkoRisk.HIGH -> CyberRed
}
