package com.example.myapplication.ui.screen.minigames

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.component.GlassCard
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun NeonHackGame(
    onGameEnd: (Int) -> Unit,
    onBack: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableFloatStateOf(30f) }
    var gameState by remember { mutableStateOf(HackGameState.START) }
    
    var nodes by remember { mutableStateOf<List<HackNode>>(emptyList()) }
    var activeNodeId by remember { mutableStateOf<Long?>(null) }
    
    val scope = rememberCoroutineScope()

    LaunchedEffect(gameState) {
        if (gameState == HackGameState.PLAYING) {
            nodes = spawnNodes(5)
            while (timeLeft > 0 && gameState == HackGameState.PLAYING) {
                delay(100)
                timeLeft -= 0.1f
            }
            gameState = HackGameState.ENDED
            onGameEnd(score)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (gameState == HackGameState.PLAYING) {
                        val currentNodes = nodes
                        val tappedNode = currentNodes.find { node ->
                            val dx = node.x - offset.x
                            val dy = node.y - offset.y
                            sqrt(dx * dx + dy * dy) < 100f // Radius check
                        }
                        
                        tappedNode?.let { node ->
                            if (node.isCorrupted) {
                                score = (score - 50).coerceAtLeast(0)
                                timeLeft -= 2f
                                nodes = currentNodes.filter { it.id != node.id } + spawnNodes(1)
                            } else {
                                score += 100
                                nodes = currentNodes.filter { it.id != node.id } + spawnNodes(1)
                                if (Random.nextFloat() > 0.7f) nodes = nodes + spawnNodes(1, true)
                            }
                        }
                    }
                }
            }
    ) {
        // Game Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            nodes.forEach { node ->
                val color = if (node.isCorrupted) PremiumPink else PremiumGreen
                drawCircle(
                    color = color.copy(alpha = 0.2f),
                    radius = 50f,
                    center = Offset(node.x, node.y)
                )
                drawCircle(
                    color = color,
                    radius = 40f,
                    center = Offset(node.x, node.y),
                    style = Stroke(width = 4f)
                )
                if (node.isCorrupted) {
                    drawLine(
                        color = color,
                        start = Offset(node.x - 20f, node.y - 20f),
                        end = Offset(node.x + 20f, node.y + 20f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = color,
                        start = Offset(node.x + 20f, node.y - 20f),
                        end = Offset(node.x - 20f, node.y + 20f),
                        strokeWidth = 4f
                    )
                }
            }
        }

        // UI Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SCORE: $score", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Text("TIME: ${timeLeft.toInt()}s", color = if (timeLeft < 5) PremiumPink else Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
            }

            if (gameState == HackGameState.START) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NEON HACK", color = PremiumGreen, fontWeight = FontWeight.Black, fontSize = 48.sp)
                        Text("Tap Green Nodes, Avoid Pink Corrupted Nodes", color = Color.White.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 32.dp))
                        Button(
                            onClick = { gameState = HackGameState.PLAYING },
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("INITIALIZE HACK", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (gameState == HackGameState.ENDED) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    GlassCard(borderColor = PremiumGreen) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            Text("HACK COMPLETE", color = PremiumGreen, fontWeight = FontWeight.Black, fontSize = 32.sp)
                            Text("FINAL SCORE: $score", color = Color.White, fontSize = 24.sp, modifier = Modifier.padding(vertical = 16.dp))
                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = PremiumGreen),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("EXIT TERMINAL", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun spawnNodes(count: Int, corrupted: Boolean = false): List<HackNode> {
    val newNodes = mutableListOf<HackNode>()
    repeat(count) {
        newNodes.add(
            HackNode(
                id = System.currentTimeMillis() + Random.nextLong(),
                x = Random.nextFloat() * 800f + 100f,
                y = Random.nextFloat() * 1200f + 200f,
                isCorrupted = corrupted || Random.nextFloat() > 0.85f
            )
        )
    }
    return newNodes
}

data class HackNode(
    val id: Long,
    val x: Float,
    val y: Float,
    val isCorrupted: Boolean
)

private enum class HackGameState { START, PLAYING, ENDED }

