package com.example.myapplication.ui.screen.minigames

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryMatchGame(
    onGameEnd: (Int) -> Unit,
    onBack: () -> Unit
) {
    val emojis = listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼")
    var cards by remember { mutableStateOf((emojis + emojis).shuffled()) }
    var flippedIndices by remember { mutableStateOf(setOf<Int>()) }
    var matchedIndices by remember { mutableStateOf(setOf<Int>()) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1B5E20))) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
                Text(
                    text = "MATCHES: ${matchedIndices.size / 2} / 8",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(cards) { index, emoji ->
                    val isFlipped = flippedIndices.contains(index) || matchedIndices.contains(index)
                    MemoryCard(
                        emoji = emoji,
                        isFlipped = isFlipped,
                        onClick = {
                            if (!isFlipped && flippedIndices.size < 2 && !gameOver) {
                                val newFlipped = flippedIndices + index
                                flippedIndices = newFlipped
                                if (newFlipped.size == 2) {
                                    scope.launch {
                                        delay(800L)
                                        val list = newFlipped.toList()
                                        if (cards[list[0]] == cards[list[1]]) {
                                            val updatedMatched = matchedIndices + newFlipped
                                            matchedIndices = updatedMatched
                                            if (updatedMatched.size == cards.size) {
                                                score = 100
                                                gameOver = true
                                                onGameEnd(score)
                                            }
                                        }
                                        flippedIndices = emptySet()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        if (gameOver) {
            GameOverPanel(
                score = matchedIndices.size / 2,
                bestScore = 8,
                coinsEarned = 100,
                onTryAgain = {
                    cards = (emojis + emojis).shuffled()
                    flippedIndices = emptySet()
                    matchedIndices = emptySet()
                    score = 0
                    gameOver = false
                },
                onBackToMenu = onBack
            )
        }
    }
}

@Composable
fun MemoryCard(emoji: String, isFlipped: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "cardFlip"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onClick() }
            .background(
                if (rotation <= 90f) Color.White.copy(alpha = 0.1f) else Color.White,
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (rotation > 90f) {
            Text(
                text = emoji,
                fontSize = 32.sp,
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            )
        } else {
            Text(
                text = "?",
                fontSize = 28.sp,
                color = Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
