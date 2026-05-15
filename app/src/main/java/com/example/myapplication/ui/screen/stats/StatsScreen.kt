package com.example.myapplication.ui.screen.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.LifetimeStats
import com.example.myapplication.domain.model.PetModel
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.screen.home.PetViewModel
import com.example.myapplication.ui.theme.*
import java.util.concurrent.TimeUnit

@Composable
fun StatsScreen(
    viewModel: PetViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val pet by viewModel.petState.collectAsState()
    val stats by viewModel.statistics.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NeonBackground(accentColor = PremiumBlue)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            ScreenHeader(
                title = "Statistics",
                subtitle = "Life Analytics",
                accentColor = PremiumBlue,
                onBack = onBack
            )

            pet?.let { currentPet ->
                LifetimeStatsGrid(currentPet, stats)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                CasinoAnalyticsSection(stats)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun LifetimeStatsGrid(pet: PetModel, stats: LifetimeStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatItemCard("Active Time", formatTime(stats.totalPlayTimeMillis), "⏱️", PremiumBlue, Modifier.weight(1f))
            StatItemCard("Coins Earned", stats.totalCoinsEarned.toString(), "💰", PremiumGold, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatItemCard("Food Eaten", stats.totalFoodEaten.toString(), "🍎", PremiumPink, Modifier.weight(1f))
            StatItemCard("Games Played", stats.miniGamesPlayed.toString(), "🎮", PremiumPurple, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatItemCard("Survival", "${calculateAge(pet.birthTimestamp)} Days", "🎂", PremiumGreen, Modifier.weight(1f))
            StatItemCard("Interactions", stats.interactionsCount.toString(), "💖", PremiumPink, Modifier.weight(1f))
        }
    }
}

@Composable
fun StatItemCard(label: String, value: String, emoji: String, accentColor: Color, modifier: Modifier = Modifier) {
    NeonCard(
        modifier = modifier,
        accentColor = accentColor
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, 
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value, 
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = accentColor.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun CasinoAnalyticsSection(stats: LifetimeStats) {
    Text(
        text = "Casino Activity",
        style = MaterialTheme.typography.labelSmall,
        color = PremiumPink.copy(alpha = 0.6f),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(12.dp))
    
    NeonCard(accentColor = PremiumPink) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            AnalyticsRow("Total Wins", stats.casinoWins.toString(), PremiumGreen)
            AnalyticsRow("Total Losses", stats.casinoLosses.toString(), PremiumRed)
            AnalyticsRow("Best Payout", "${stats.biggestCasinoWin} CR", PremiumBlue)
            AnalyticsRow("Jackpots", stats.casinoJackpots.toString(), PremiumGold)
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(12.dp))
            
            val winRate = if (stats.gamblingExposureCount > 0) {
                (stats.casinoWins.toFloat() / stats.gamblingExposureCount * 100).toInt()
            } else 0
            
            AnalyticsRow("Win Rate", "$winRate%", PremiumBlue)
            AnalyticsRow("Risk Level", "${stats.addictionIntensity.toInt()}%", PremiumPurple)
            
            if (stats.addictionIntensity > 50f) {
                Spacer(modifier = Modifier.height(16.dp))
                NeonCard(accentColor = PremiumRed) {
                    Text(
                        "Caution: High Risk Play Detected",
                        color = PremiumRed,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.labelSmall)
        Text(value, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
    }
}

private fun formatTime(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    return "${hours}H ${minutes}M"
}

private fun calculateAge(birthTimestamp: Long): Long {
    val diff = System.currentTimeMillis() - birthTimestamp
    return TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(1)
}

