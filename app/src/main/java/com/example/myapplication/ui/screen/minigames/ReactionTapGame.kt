package com.example.myapplication.ui.screen.minigames

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class TargetType {
    NORMAL, CRITICAL, PENALTY, FAKE
}

data class ReactionTarget(
    val id: Long,
    var x: Dp,
    var y: Dp,
    val size: Dp,
    val duration: Long,
    val type: TargetType,
    val spawnTime: Long = System.currentTimeMillis()
)

@Composable
fun ReactionTapGame(
    onGameEnd: (Int) -> Unit,
    onExit: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var bestScore by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var gameOver by remember { mutableStateOf(false) }
    var targets by remember { mutableStateOf(listOf<ReactionTarget>()) }
    
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Spawning Logic
    LaunchedEffect(gameOver) {
        if (!gameOver) {
            while (lives > 0) {
                val difficultyScale = (score / 500f).coerceIn(0f, 1f)
                val nextSpawnDelay = (600L..1200L).random() - (difficultyScale * 400).toLong()
                delay(maxOf(200L, nextSpawnDelay))
                
                val typeRoll = Random.nextFloat()
                val type = when {
                    typeRoll < 0.10f -> TargetType.FAKE
                    typeRoll < 0.18f -> TargetType.PENALTY
                    typeRoll < 0.25f -> TargetType.CRITICAL
                    else -> TargetType.NORMAL
                }

                val duration = (1200L..2000L).random() - (difficultyScale * 600).toLong()
                
                val newTarget = ReactionTarget(
                    id = System.nanoTime(),
                    x = (40..(screenWidth.value.toInt() - 80)).random().dp,
                    y = (100..(screenHeight.value.toInt() - 200)).random().dp,
                    size = (60..90).random().dp,
                    duration = maxOf(400L, duration),
                    type = type
                )
                targets = targets + newTarget
            }
            gameOver = true
            if (score > bestScore) bestScore = score
            onGameEnd(score)
        }
    }

    // Moving Targets Logic
    LaunchedEffect(gameOver) {
        while (!gameOver) {
            delay(32) // Reduced frequency for movement
            val time = System.currentTimeMillis() / 1000f
            
            // Only update if there are CRITICAL targets to move
            if (targets.any { it.type == TargetType.CRITICAL }) {
                targets = targets.map { target ->
                    if (target.type == TargetType.CRITICAL) {
                        val offsetX = sin(time * 5f) * 2f
                        val offsetY = cos(time * 5f) * 2f
                        target.copy(x = target.x + offsetX.dp, y = target.y + offsetY.dp)
                    } else {
                        target
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0E14))) {
        // Grid Background Effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 40.dp.toPx()
            for (x in 0..size.width.toInt() step gridSize.toInt()) {
                drawLine(Color.White.copy(alpha = 0.05f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height))
            }
            for (y in 0..size.height.toInt() step gridSize.toInt()) {
                drawLine(Color.White.copy(alpha = 0.05f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()))
            }
        }

        // Targets
        targets.forEach { target ->
            key(target.id) {
                TargetComponent(
                    target = target,
                    onTap = { reactionTime ->
                        when (target.type) {
                            TargetType.NORMAL -> {
                                val speedBonus = (1.0f - (reactionTime.toFloat() / target.duration)).coerceIn(0f, 1f)
                                val points = (10 + (speedBonus * 20)).toInt()
                                score += points
                                combo++
                            }
                            TargetType.CRITICAL -> {
                                score += 50
                                combo += 2
                            }
                            TargetType.PENALTY -> {
                                score = (score - 50).coerceAtLeast(0)
                                combo = 0
                                lives--
                            }
                            TargetType.FAKE -> {
                                lives--
                                combo = 0
                            }
                        }
                        if (combo > maxCombo) maxCombo = combo
                        targets = targets.filter { it.id != target.id }
                    },
                    onExpire = {
                        if (target.type == TargetType.NORMAL || target.type == TargetType.CRITICAL) {
                            lives--
                            combo = 0
                        }
                        targets = targets.filter { it.id != target.id }
                    }
                )
            }
        }

        // HUD
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onExit,
                modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = score.toString().padStart(5, '0'),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    letterSpacing = 2.sp
                )
                Row {
                    repeat(3) { index ->
                        Text(
                            text = if (index < lives) "❤️" else "🖤",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
                if (combo > 1) {
                    Text(
                        "COMBO x$combo",
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (gameOver) {
            GameOverPanel(
                score = score,
                bestScore = bestScore,
                coinsEarned = score / 3,
                onTryAgain = {
                    score = 0
                    combo = 0
                    maxCombo = 0
                    lives = 3
                    targets = emptyList()
                    gameOver = false
                },
                onBackToMenu = onExit
            )
        }
    }
}

@Composable
fun TargetComponent(
    target: ReactionTarget,
    onTap: (Long) -> Unit,
    onExpire: () -> Unit
) {
    var isTapped by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse)
    )

    val progress = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        progress.animateTo(0f, tween(target.duration.toInt(), easing = LinearEasing))
        if (!isTapped) {
            onExpire()
        }
    }

    val color = when (target.type) {
        TargetType.NORMAL -> Color(0xFFEF5350)
        TargetType.CRITICAL -> Color(0xFFFFD700)
        TargetType.PENALTY -> Color(0xFF9E9E9E)
        TargetType.FAKE -> Color(0xFF42A5F5)
    }

    Box(
        modifier = Modifier
            .offset(target.x, target.y)
            .size(target.size)
            .scale(pulseScale)
            .graphicsLayer {
                alpha = if (isTapped) 0f else 1f
            }
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(color.copy(alpha = 0.8f), color)))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isTapped) {
                    isTapped = true
                    onTap(System.currentTimeMillis() - target.spawnTime)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Expiration Ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color.White.copy(alpha = 0.5f),
                startAngle = -90f,
                sweepAngle = 360f * progress.value,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx())
            )
        }
        
        Text(
            text = when(target.type) {
                TargetType.NORMAL -> "TAP"
                TargetType.CRITICAL -> "!!! "
                TargetType.PENALTY -> "NO"
                TargetType.FAKE -> "..."
            },
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
        )
    }
}
