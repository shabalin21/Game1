package com.example.myapplication.ui.screen.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ui.component.GlassCard
import com.example.myapplication.ui.component.ScreenHeader
import com.example.myapplication.ui.screen.home.PetViewModel
import com.example.myapplication.ui.theme.*

@Composable
fun MinigameHub(
    onPlayTapRush: () -> Unit,
    onPlayMemoryMatch: () -> Unit,
    onPlayReactionTap: () -> Unit,
    onPlayNeonDodge: () -> Unit,
    onPlayNeonHack: () -> Unit,
    onPlayVoidRunner: () -> Unit,
    onBack: () -> Unit,
    viewModel: PetViewModel = hiltViewModel()
) {
    val petSnapshot by viewModel.petState.collectAsState()
    
    // Survival Rules: Block if hunger or energy is 0
    val canPlay = petSnapshot?.let { 
        it.stats.hunger > 0.1f && it.stats.energy > 0.1f 
    } ?: true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Decorative background elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                Brush.radialGradient(listOf(NeonBlue.copy(alpha = 0.05f), Color.Transparent)),
                radius = size.width,
                center = Offset(size.width, 0f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            ScreenHeader(
                title = "ARCADE_HUB",
                subtitle = "NEURAL_SENSORY_TASKS",
                accentColor = NeonBlue,
                onBack = onBack
            )
            
            if (!canPlay) {
                Surface(
                    color = NeonPink.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "LOCKED: Pet is too weak. Rest or feed them!",
                        modifier = Modifier.padding(12.dp),
                        color = NeonPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    MinigameCard(
                        title = "NEON HACK",
                        description = "Fast reaction node connection.",
                        emoji = "📠",
                        reward = "COINS / INTEL",
                        difficulty = "MEDIUM",
                        color = NeonGreen,
                        enabled = canPlay,
                        onClick = onPlayNeonHack
                    )
                }
                item {
                    MinigameCard(
                        title = "VOID RUNNER",
                        description = "Endless dodging in the deep void.",
                        emoji = "🚀",
                        reward = "COINS / XP",
                        difficulty = "HARD",
                        color = NeonBlue,
                        enabled = canPlay,
                        onClick = onPlayVoidRunner
                    )
                }
                item {
                    MinigameCard(
                        title = "TAP RUSH",
                        description = "Hyper-fast fruit slicing action.",
                        emoji = "⚡",
                        reward = "COINS",
                        difficulty = "EASY",
                        color = NeonOrange,
                        enabled = canPlay,
                        onClick = onPlayTapRush
                    )
                }
                item {
                    MinigameCard(
                        title = "NEON DODGE",
                        description = "High-speed avoidance in retro-future.",
                        emoji = "🌌",
                        reward = "COINS / AGILITY",
                        difficulty = "HARD",
                        color = NeonBlue,
                        enabled = canPlay,
                        onClick = onPlayNeonDodge
                    )
                }
                item {
                    MinigameCard(
                        title = "REACTION TAP",
                        description = "Test your reflexes with shrinking targets.",
                        emoji = "🎯",
                        reward = "COINS / FOCUS",
                        difficulty = "MEDIUM",
                        color = NeonPink,
                        enabled = canPlay,
                        onClick = onPlayReactionTap
                    )
                }
                item {
                    MinigameCard(
                        title = "MEMORY MATCH",
                        description = "Classic brain training with pet emojis.",
                        emoji = "🧠",
                        reward = "COINS / MEMORY",
                        difficulty = "EASY",
                        color = NeonGreen,
                        enabled = canPlay,
                        onClick = onPlayMemoryMatch
                    )
                }
            }
        }
    }
}

@Composable
fun MinigameCard(
    title: String,
    description: String,
    emoji: String,
    reward: String,
    difficulty: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
        borderColor = if (enabled) color else Color.White.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).graphicsLayer { alpha = if (enabled) 1f else 0.3f },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 28.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "DIF: $difficulty", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "REW: $reward", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            if (enabled) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
