package com.example.myapplication.ui.screen.casino.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.casino.model.CoinFlipResult
import com.example.myapplication.domain.casino.model.CoinSide
import com.example.myapplication.ui.component.NeonButton
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CoinFlipView(
    state: CoinFlipResult?,
    coins: Int,
    onFlip: (Int, CoinSide) -> Unit
) {
    var isFlipping by remember { mutableStateOf(false) }
    var selectedSide by remember { mutableStateOf(CoinSide.HEADS) }
    var selectedWager by remember { mutableIntStateOf(100) }

    LaunchedEffect(state) {
        if (state != null) {
            delay(800)
            isFlipping = false
        }
    }

    CasinoGameCard(accentColor = NeonPink) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GameSectionHeader("Double or Nothing", color = NeonPink)
            
            Spacer(modifier = Modifier.height(24.dp))

            BoxWithConstraints(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                val coinSize = (maxWidth / 2f).coerceIn(120.dp, 180.dp)
                
                CyberCoin(
                    side = state?.side ?: selectedSide,
                    isFlipping = isFlipping,
                    size = coinSize
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SIDE SELECTOR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SideButton(
                    side = CoinSide.HEADS,
                    isSelected = selectedSide == CoinSide.HEADS,
                    onClick = { selectedSide = CoinSide.HEADS },
                    modifier = Modifier.weight(1f),
                    enabled = !isFlipping
                )
                SideButton(
                    side = CoinSide.TAILS,
                    isSelected = selectedSide == CoinSide.TAILS,
                    onClick = { selectedSide = CoinSide.TAILS },
                    modifier = Modifier.weight(1f),
                    enabled = !isFlipping
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BETTING CONTROLS
            BettingControls(
                selectedWager = selectedWager,
                onWagerChange = { selectedWager = it },
                onAction = {
                    isFlipping = true
                    onFlip(selectedWager, selectedSide)
                },
                actionText = if (isFlipping) "FLIPPING..." else "EXECUTE FLIP",
                enabled = !isFlipping && coins >= selectedWager,
                accentColor = NeonPink,
                maxCoins = coins
            )

            // RESULT BANNER
            Box(modifier = Modifier.height(60.dp), contentAlignment = Alignment.Center) {
                this@Column.AnimatedVisibility(
                    visible = state != null && !isFlipping,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut()
                ) {
                    if (state != null) {
                        CoinFlipResultBanner(isWin = state.isWin, payout = state.payout)
                    }
                }
            }
        }
    }
}

@Composable
fun CyberCoin(
    side: CoinSide,
    isFlipping: Boolean,
    size: androidx.compose.ui.unit.Dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CoinSpin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1080f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFlipping) 1.1f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "Scale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                if (isFlipping) {
                    rotationY = rotation
                }
            }
            .shadow(
                elevation = if (isFlipping) 32.dp else 12.dp,
                shape = CircleShape,
                ambientColor = NeonPink,
                spotColor = NeonPink
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(SurfaceDark, Color.Black)
                )
            )
            .border(2.dp, NeonPink.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (side == CoinSide.HEADS) "HEADS" else "TAILS",
            style = MaterialTheme.typography.headlineSmall,
            color = if (isFlipping) NeonPink.copy(alpha = 0.2f) else Color.White,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .border(1.dp, NeonPink.copy(alpha = 0.1f), CircleShape)
        )
    }
}

@Composable
fun SideButton(
    side: CoinSide,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) NeonPink.copy(alpha = 0.2f) else SurfaceDark,
        label = "SideBg"
    )
    
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(
                1.dp, 
                if (isSelected) NeonPink else Color.White.copy(alpha = 0.05f), 
                RoundedCornerShape(6.dp)
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = side.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun CoinFlipResultBanner(isWin: Boolean, payout: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (isWin) "Correct!" else "Better luck next time",
            color = if (isWin) NeonGreen else NeonRed,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 1.sp
        )
        if (isWin) {
            Text(
                text = "+$payout CR",
                color = NeonCyan,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}
