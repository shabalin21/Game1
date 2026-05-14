package com.example.myapplication.ui.screen.minigames

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.random.Random

data class Obstacle(
    val id: Long,
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)

data class Pickup(
    val id: Long,
    var x: Float,
    var y: Float,
    val size: Float = 20f
)

@Composable
fun NeonDodgeGame(
    onGameEnd: (Int) -> Unit,
    onBack: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var bestScore by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var playerX by remember { mutableFloatStateOf(0f) }
    var playerY by remember { mutableFloatStateOf(0f) }
    
    var obstacles by remember { mutableStateOf<List<Obstacle>>(emptyList()) }
    var pickups by remember { mutableStateOf<List<Pickup>>(emptyList()) }
    
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    
    // Initialize player position
    LaunchedEffect(Unit) {
        playerX = screenWidthPx / 2
        playerY = screenHeightPx * 0.8f
    }

    // Spawning Loop
    LaunchedEffect(gameOver) {
        if (!gameOver) {
            while (true) {
                val difficultyScale = (score / 1000f).coerceIn(0f, 1f)
                delay(maxOf(150L, 500L - (difficultyScale * 350).toLong()))
                
                val newObstacle = Obstacle(
                    id = System.nanoTime(),
                    x = Random.nextFloat() * screenWidthPx,
                    y = -100f,
                    size = 40f + Random.nextFloat() * 60f,
                    speed = 8f + (difficultyScale * 12f),
                    color = listOf(Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7)).random()
                )
                obstacles = obstacles + newObstacle

                if (Random.nextFloat() < 0.2f) {
                    val newPickup = Pickup(
                        id = System.nanoTime(),
                        x = Random.nextFloat() * screenWidthPx,
                        y = -100f
                    )
                    pickups = pickups + newPickup
                }
            }
        }
    }

    // Game Loop (Physics & Collision)
    LaunchedEffect(gameOver) {
        if (!gameOver) {
            var lastTime = System.currentTimeMillis()
            while (true) {
                val currentTime = System.currentTimeMillis()
                val deltaTime = (currentTime - lastTime) / 16.67f
                lastTime = currentTime

                // Physics update on a local copy
                val currentObstacles = obstacles
                val updatedObstacles = currentObstacles.map { obs ->
                    obs.copy(y = obs.y + obs.speed * deltaTime)
                }.filter { it.y <= screenHeightPx + 100 }

                // Check for score and game over
                val removedCount = currentObstacles.size - updatedObstacles.size
                score += removedCount * 10

                // Collision Detection
                val collided = updatedObstacles.any { obs ->
                    val dx = playerX - obs.x
                    val dy = playerY - obs.y
                    val distance = sqrt(dx * dx + dy * dy)
                    distance < (obs.size / 2 + 20f)
                }

                if (collided) {
                    gameOver = true
                    if (score > bestScore) bestScore = score
                    onGameEnd(score)
                    break
                }

                // Atomic update
                obstacles = updatedObstacles

                // Update Pickups
                val currentPickups = pickups
                val updatedPickups = currentPickups.map { pick ->
                    pick.copy(y = pick.y + 6f * deltaTime)
                }.filter { pick ->
                    val dx = playerX - pick.x
                    val dy = playerY - pick.y
                    val distance = sqrt(dx * dx + dy * dy)
                    val collected = distance < 40f
                    if (collected) score += 100
                    pick.y <= screenHeightPx + 100 && !collected
                }
                
                pickups = updatedPickups

                delay(16)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    if (!gameOver) {
                        playerX = (playerX + dragAmount.x).coerceIn(40f, screenWidthPx - 40f)
                        playerY = (playerY + dragAmount.y).coerceIn(40f, screenHeightPx - 40f)
                        change.consume()
                    }
                }
            }
    ) {
        // Neon Background Grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSpacing = 60.dp.toPx()
            for (i in 0..(size.width / gridSpacing).toInt()) {
                drawLine(
                    Color(0xFF00E5FF).copy(alpha = 0.1f),
                    Offset(i * gridSpacing, 0f),
                    Offset(i * gridSpacing, size.height),
                    strokeWidth = 1f
                )
            }
            for (i in 0..(size.height / gridSpacing).toInt()) {
                drawLine(
                    Color(0xFF00E5FF).copy(alpha = 0.1f),
                    Offset(0f, i * gridSpacing),
                    Offset(size.width, i * gridSpacing),
                    strokeWidth = 1f
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Player (Glowing Orb)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF), Color(0xFF00E5FF).copy(alpha = 0f)),
                    center = Offset(playerX, playerY),
                    radius = 40f
                )
            )
            drawCircle(
                color = Color.White,
                radius = 12f,
                center = Offset(playerX, playerY)
            )

            // Draw Obstacles
            obstacles.forEach { obs ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(obs.color, obs.color.copy(alpha = 0f)),
                        center = Offset(obs.x, obs.y),
                        radius = obs.size
                    )
                )
                drawCircle(
                    color = obs.color,
                    radius = obs.size / 3,
                    center = Offset(obs.x, obs.y),
                    style = Stroke(width = 4f)
                )
            }

            // Draw Pickups
            pickups.forEach { pick ->
                withTransform({
                    translate(pick.x, pick.y)
                    rotate((System.currentTimeMillis() % 1000) / 1000f * 360f)
                }) {
                    drawRect(
                        color = Color(0xFFFFD700),
                        topLeft = Offset(-10f, -10f),
                        size = androidx.compose.ui.geometry.Size(20f, 20f),
                        style = Stroke(width = 3f)
                    )
                }
            }
        }

        // HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Text(
                text = score.toString().padStart(5, '0'),
                color = Color(0xFF00E5FF),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }

        if (gameOver) {
            GameOverPanel(
                score = score,
                bestScore = bestScore,
                coinsEarned = score / 10,
                onTryAgain = {
                    score = 0
                    obstacles = emptyList()
                    pickups = emptyList()
                    playerX = screenWidthPx / 2
                    playerY = screenHeightPx * 0.8f
                    gameOver = false
                },
                onBackToMenu = onBack
            )
        }
    }
}
