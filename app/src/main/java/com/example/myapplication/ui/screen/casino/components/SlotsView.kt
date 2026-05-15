package com.example.myapplication.ui.screen.casino.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.casino.model.SlotResult
import com.example.myapplication.ui.component.NeonButton
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SlotsView(
    state: SlotResult?,
    coins: Int,
    onSpin: (Int) -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    var selectedWager by remember { mutableIntStateOf(50) }

    LaunchedEffect(state) {
        if (state != null) {
            delay(1000) // Keep spinning for a bit
            isSpinning = false
        }
    }

    CasinoGameCard(accentColor = PremiumGold) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GameSectionHeader("Classic Slots", color = PremiumGold)
            
            Spacer(modifier = Modifier.height(24.dp))

            // REEL ENCLOSURE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.dp, PremiumGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        SlotReel(
                            symbol = state?.reels?.getOrNull(index)?.icon ?: "❓",
                            isSpinning = isSpinning,
                            index = index,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Horizontal scanline overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(PremiumGold.copy(alpha = 0.1f))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BETTING CONTROLS
            BettingControls(
                selectedWager = selectedWager,
                onWagerChange = { selectedWager = it },
                onAction = {
                    isSpinning = true
                    onSpin(selectedWager)
                },
                actionText = if (isSpinning) "SPINNING..." else "SPIN",
                enabled = !isSpinning && coins >= selectedWager,
                accentColor = PremiumGold,
                maxCoins = coins
            )

            // PAYOUT AREA
            Box(modifier = Modifier.height(80.dp), contentAlignment = Alignment.Center) {
                this@Column.AnimatedVisibility(
                    visible = state != null && !isSpinning,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut()
                ) {
                    if (state != null) {
                        SlotResultBanner(payout = state.payout, isJackpot = state.isJackpot)
                    }
                }
            }
        }
    }
}

@Composable
fun SlotReel(
    symbol: String,
    isSpinning: Boolean,
    index: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ReelSpin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(150 + (index * 50), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(
                width = 1.dp,
                color = if (isSpinning) PremiumGold.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isSpinning) "⚡" else symbol,
            fontSize = 32.sp,
            modifier = Modifier.graphicsLayer {
                if (isSpinning) {
                    rotationX = rotation
                    alpha = 0.5f
                }
            }
        )
        
        // Reflection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.05f), Color.Transparent, Color.Black.copy(alpha = 0.1f))
                    )
                )
        )
    }
}

@Composable
fun SlotResultBanner(payout: Int, isJackpot: Boolean) {
    val isWin = payout > 0
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (isJackpot) "JACKPOT!" else if (isWin) "You Won!" else "Try Again",
            color = if (isJackpot) PremiumGold else if (isWin) PremiumGreen else Color.White.copy(alpha = 0.3f),
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 1.sp
        )
        if (isWin) {
            Text(
                text = "+$payout CR",
                color = PremiumCyan,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}

