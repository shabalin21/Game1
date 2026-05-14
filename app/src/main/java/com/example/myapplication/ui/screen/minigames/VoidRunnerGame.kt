package com.example.myapplication.ui.screen.minigames

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.component.GlassCard
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun VoidRunnerGame(
    onGameEnd: (Int) -> Unit,
    onBack: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var gameState by remember { mutableStateOf(VoidGameState.START) }
    var playerX by remember { mutableFloatStateOf(0.5f) } // 0f to 1f
    
    var obstacles by remember { mutableStateOf<List<VoidObstacle>>(emptyList()) }
    var shards by remember { mutableStateOf<List<VoidShard>>(emptyList()) }
    
    var gameSpeed by remember { mutableFloatStateOf(0.01f) }

    LaunchedEffect(gameState) {
        if (gameState == VoidGameState.PLAYING) {
            obstacles = emptyList()
            shards = emptyList()
            score = 0
            gameSpeed = 0.01f
            
            var lastTimeNanos = 0L
            
            while (gameState == VoidGameState.PLAYING) {
                withFrameNanos { frameTimeNanos ->
                    if (lastTimeNanos == 0L) {
                        lastTimeNanos = frameTimeNanos
                        return@withFrameNanos
                    }
                    
                    val deltaTime = (frameTimeNanos - lastTimeNanos) / 1_000_000_000f
                    lastTimeNanos = frameTimeNanos
                    
                    // Normalize logic to ~60fps (1/60 = 0.0166)
                    val timeScale = deltaTime / (1f / 60f)
                
                    // Physics update
                    val currentObstacles = obstacles
                    val currentShards = shards
                    
                    // Spawn
                    val newObstacles = if (Random.nextFloat() < 0.03f * timeScale) {
                        currentObstacles + VoidObstacle(Random.nextFloat(), 0f)
                    } else currentObstacles

                    val newShards = if (Random.nextFloat() < 0.01f * timeScale) {
                        currentShards + VoidShard(Random.nextFloat(), 0f)
                    } else currentShards
                    
                    // Move & Collision
                    val updatedObstacles = mutableListOf<VoidObstacle>()
                    var collisionDetected = false
                    val playerY = 0.85f
                    
                    newObstacles.forEach { obs ->
                        val movement = gameSpeed * timeScale
                        val newY = obs.y + movement
                        
                        if (newY <= 1.1f) {
                            updatedObstacles.add(obs.copy(y = newY))
                            // Collision check (Player is at y=0.85f)
                            // Fix: Tunneling check - check if it crossed playerY this frame
                            val crossedPlayer = obs.y <= playerY && newY >= playerY
                            if (crossedPlayer && abs(obs.x - playerX) < 0.15f) {
                                collisionDetected = true
                            }
                        }
                    }
                    
                    if (collisionDetected) {
                        gameState = VoidGameState.ENDED
                        onGameEnd(score)
                    }
                    
                    obstacles = updatedObstacles
                    
                    val updatedShards = mutableListOf<VoidShard>()
                    newShards.forEach { shard ->
                        val movement = gameSpeed * timeScale
                        val newY = shard.y + movement
                        if (newY <= 1.1f) {
                            val crossedPlayer = shard.y <= playerY && newY >= playerY
                            if (crossedPlayer && abs(shard.x - playerX) < 0.1f) {
                                score += 500
                            } else {
                                updatedShards.add(shard.copy(y = newY))
                            }
                        }
                    }
                    shards = updatedShards
                    
                    score += 1
                    gameSpeed += 0.000005f * timeScale
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    playerX = (playerX + dragAmount.x / size.width).coerceIn(0.1f, 0.9f)
                }
            }
    ) {
        // Game Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            // Draw Player
            drawRect(
                color = NeonBlue,
                topLeft = Offset(playerX * w - 30f, 0.85f * h - 30f),
                size = Size(60f, 60f)
            )
            drawRect(
                color = NeonBlue.copy(alpha = 0.3f),
                topLeft = Offset(playerX * w - 40f, 0.85f * h - 40f),
                size = Size(80f, 80f)
            )
            
            // Draw Obstacles
            obstacles.forEach { obs ->
                drawRect(
                    color = NeonPink,
                    topLeft = Offset(obs.x * w - 50f, obs.y * h - 25f),
                    size = Size(100f, 50f)
                )
            }
            
            // Draw Shards
            shards.forEach { shard ->
                drawCircle(
                    color = Color.Yellow,
                    center = Offset(shard.x * w, shard.y * h),
                    radius = 20f
                )
            }
        }

        // UI Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SCORE: $score", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)

            if (gameState == VoidGameState.START) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("VOID RUNNER", color = NeonBlue, fontWeight = FontWeight.Black, fontSize = 48.sp)
                        Text("Drag to Move. Avoid Pink Walls. Collect Yellow Shards.", color = Color.White.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 32.dp))
                        Button(
                            onClick = { gameState = VoidGameState.PLAYING },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("START RUN", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (gameState == VoidGameState.ENDED) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    GlassCard(borderColor = NeonPink) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            Text("RUN OVER", color = NeonPink, fontWeight = FontWeight.Black, fontSize = 32.sp)
                            Text("FINAL SCORE: $score", color = Color.White, fontSize = 24.sp, modifier = Modifier.padding(vertical = 16.dp))
                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("EXIT VOID", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class VoidObstacle(val x: Float, val y: Float)
private data class VoidShard(val x: Float, val y: Float)
private enum class VoidGameState { START, PLAYING, ENDED }
