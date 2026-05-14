package com.example.myapplication.ui.screen.minigames

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

enum class TapObjectType {
    NORMAL, GOLD, BOMB, RARE
}

data class GameStateItem(
    val id: Long,
    var x: Float,
    var y: Float,
    val emoji: String,
    val speed: Float,
    val type: TapObjectType,
    val rotation: Float = Random.nextFloat() * 360f,
    val rotationSpeed: Float = (Random.nextFloat() - 0.5f) * 5f
)

data class FloatingText(
    val id: Long,
    val text: String,
    val x: Float,
    var y: Float,
    var alpha: Float = 1f,
    val color: Color = Color.White,
    val scale: Float = 1f
)

data class Particle(
    val id: Long,
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    var life: Float = 1f
)

@Composable
fun TapRushGame(
    onGameEnd: (Int) -> Unit,
    onBack: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var bestScore by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf<List<GameStateItem>>(emptyList()) }
    var floatingTexts by remember { mutableStateOf<List<FloatingText>>(emptyList()) }
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    
    val textMeasurer = rememberTextMeasurer()
    val emojiStyle = TextStyle(fontSize = 50.sp)
    val floatStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Black)

    // Screen Shake state
    var shakeIntensity by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    fun triggerShake(intensity: Float) {
        shakeIntensity = intensity
        scope.launch {
            while (shakeIntensity > 0.1f) {
                delay(16)
                shakeIntensity *= 0.85f
            }
            shakeIntensity = 0f
        }
    }

    fun spawnParticles(x: Float, y: Float, color: Color, count: Int = 10) {
        val newParticles = mutableListOf<Particle>()
        repeat(count) {
            newParticles.add(
                Particle(
                    id = Random.nextLong(),
                    x = x,
                    y = y,
                    vx = (Random.nextFloat() - 0.5f) * 15f,
                    vy = (Random.nextFloat() - 0.5f) * 15f,
                    color = color
                )
            )
        }
        particles = particles + newParticles
    }

    // Spawning Loop
    LaunchedEffect(gameOver) {
        if (gameOver) return@LaunchedEffect
        while (true) {
            val baseDelay = 1000L
            val difficultyReduction = (score * 2L).coerceAtMost(700L)
            delay(maxOf(250L, baseDelay - difficultyReduction))
            
            val typeRoll = Random.nextFloat()
            val type = when {
                typeRoll < 0.08f -> TapObjectType.BOMB
                typeRoll < 0.14f -> TapObjectType.GOLD
                typeRoll < 0.16f -> TapObjectType.RARE
                else -> TapObjectType.NORMAL
            }
            
            val emoji = when (type) {
                TapObjectType.NORMAL -> listOf("🍎", "🍕", "🍔", "🍣", "🍦", "🍇", "🍓").random()
                TapObjectType.GOLD -> "💰"
                TapObjectType.BOMB -> "💣"
                TapObjectType.RARE -> "💎"
            }

            val newItem = GameStateItem(
                id = Random.nextLong(),
                x = 50f + Random.nextFloat() * (screenWidthPx - 150f),
                y = -100f,
                emoji = emoji,
                speed = (7f + (score / 15f)) * (if (type == TapObjectType.RARE) 1.4f else 1f),
                type = type
            )
            items = items + newItem
        }
    }

    // Game Loop (Physics & State)
    LaunchedEffect(gameOver) {
        if (gameOver) return@LaunchedEffect
        var lastTime = System.currentTimeMillis()
        
        while (true) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = (currentTime - lastTime) / 16.67f 
            lastTime = currentTime
            
            // Items Physics
            val currentItems = items
            val updatedItems = currentItems.mapNotNull { item ->
                val newY = item.y + (item.speed * deltaTime)
                
                if (newY > screenHeightPx) {
                    if (item.type != TapObjectType.BOMB) {
                        gameOver = true
                        if (score > bestScore) bestScore = score
                        onGameEnd(score)
                        null
                    } else {
                        null
                    }
                } else {
                    item.copy(y = newY, rotation = item.rotation + item.rotationSpeed)
                }
            }
            if (gameOver) break
            items = updatedItems

            // Particles Physics
            particles = particles.mapNotNull { p ->
                val newLife = p.life - 0.03f * deltaTime
                if (newLife <= 0) null
                else p.copy(x = p.x + p.vx * deltaTime, y = p.y + p.vy * deltaTime, life = newLife)
            }

            // Floating Text Physics
            floatingTexts = floatingTexts.mapNotNull { ft ->
                val newAlpha = ft.alpha - (0.02f * deltaTime)
                if (newAlpha <= 0) null
                else ft.copy(y = ft.y - (3f * deltaTime), alpha = newAlpha)
            }

            delay(16L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (gameOver) return@detectTapGestures
                    
                    val currentItems = items
                    // ACCURATE COLLISION WITH EXPANDED HITBOXES (80px radius)
                    val hitIndex = currentItems.indexOfLast { item ->
                        val centerX = item.x + 40f
                        val centerY = item.y + 40f
                        val dx = offset.x - centerX
                        val dy = offset.y - centerY
                        (dx * dx + dy * dy) < 10000f // ~100px radius hitbox
                    }
                    
                    if (hitIndex != -1) {
                        val tapped = currentItems[hitIndex]
                        items = currentItems.filterIndexed { index, _ -> index != hitIndex }
                        
                        when (tapped.type) {
                            TapObjectType.NORMAL -> {
                                combo++
                                val points = 10 + (combo / 5) * 5
                                score += points
                                spawnParticles(tapped.x + 40, tapped.y + 40, Color(0xFF4ECDC4))
                                floatingTexts = floatingTexts + FloatingText(Random.nextLong(), "+$points", tapped.x, tapped.y, color = Color.White)
                            }
                            TapObjectType.GOLD -> {
                                combo++
                                val points = 100
                                score += points
                                triggerShake(5f)
                                spawnParticles(tapped.x + 40, tapped.y + 40, Color.Yellow, 20)
                                floatingTexts = floatingTexts + FloatingText(Random.nextLong(), "GOLD!!", tapped.x, tapped.y, color = Color.Yellow, scale = 1.5f)
                            }
                            TapObjectType.RARE -> {
                                combo++
                                val points = 500
                                score += points
                                triggerShake(10f)
                                spawnParticles(tapped.x + 40, tapped.y + 40, Color.Cyan, 30)
                                floatingTexts = floatingTexts + FloatingText(Random.nextLong(), "LEGENDARY!", tapped.x, tapped.y, color = Color.Cyan, scale = 2f)
                            }
                            TapObjectType.BOMB -> {
                                combo = 0
                                score = (score - 200).coerceAtLeast(0)
                                triggerShake(20f)
                                spawnParticles(tapped.x + 40, tapped.y + 40, Color.Red, 40)
                                floatingTexts = floatingTexts + FloatingText(Random.nextLong(), "KABOOM!", tapped.x, tapped.y, color = Color.Red)
                            }
                        }
                        if (combo > maxCombo) maxCombo = combo
                    } else {
                        combo = 0
                        triggerShake(2f)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val shakeX = if (shakeIntensity > 0) (Random.nextFloat() - 0.5f) * shakeIntensity else 0f
            val shakeY = if (shakeIntensity > 0) (Random.nextFloat() - 0.5f) * shakeIntensity else 0f

            withTransform({
                translate(shakeX, shakeY)
            }) {
                // Draw Items
                items.forEach { item ->
                    withTransform({
                        rotate(item.rotation, pivot = Offset(item.x + 40f, item.y + 40f))
                    }) {
                        drawText(
                            textMeasurer = textMeasurer,
                            text = item.emoji,
                            topLeft = Offset(item.x, item.y),
                            style = emojiStyle
                        )
                    }
                }

                // Draw Particles
                particles.forEach { p ->
                    drawCircle(
                        color = p.color.copy(alpha = p.life),
                        radius = 6f * p.life,
                        center = Offset(p.x, p.y)
                    )
                }

                // Draw Floating Texts
                floatingTexts.forEach { ft ->
                    drawText(
                        textMeasurer = textMeasurer,
                        text = ft.text,
                        topLeft = Offset(ft.x, ft.y),
                        style = floatStyle.copy(
                            color = ft.color.copy(alpha = ft.alpha),
                            fontSize = (24 * ft.scale).sp,
                            shadow = Shadow(Color.Black, blurRadius = 4f)
                        )
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
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = score.toString().padStart(6, '0'),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                
                AnimatedVisibility(
                    visible = combo > 1,
                    enter = slideInHorizontally { it } + fadeIn(),
                    exit = slideOutHorizontally { it } + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .background(Color(0xFF4ECDC4))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "COMBO x$combo",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        if (gameOver) {
            GameOverPanel(
                score = score,
                bestScore = bestScore,
                coinsEarned = score / 5,
                onTryAgain = {
                    items = emptyList()
                    floatingTexts = emptyList()
                    particles = emptyList()
                    score = 0
                    combo = 0
                    maxCombo = 0
                    gameOver = false
                },
                onBackToMenu = onBack
            )
        }
    }
}
