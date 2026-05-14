package com.example.myapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.TimeOfDay
import com.example.myapplication.domain.model.Weather
import com.example.myapplication.domain.model.WorldState
import com.example.myapplication.ui.theme.*

@Composable
fun WorldStatusBar(
    world: WorldState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time & Routine Info
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (world.timeOfDay == TimeOfDay.NIGHT) PremiumBlue else PremiumGold,
                        CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = world.timeOfDay.name,
                style = Typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold
            )
        }

        // Weather & Temp
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = when(world.weather) {
                    Weather.SUNNY -> "☀️"
                    Weather.RAINY -> "🌧️"
                    Weather.CLOUDY -> "☁️"
                    Weather.STORMY -> "⛈️"
                },
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = "${world.temperature.toInt()}°C",
                style = Typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun LifestyleRankCard(
    points: Long,
    modifier: Modifier = Modifier
) {
    val rank = when {
        points > 100000 -> "EXECUTIVE"
        points > 50000 -> "ELITE"
        points > 20000 -> "PROFESSIONAL"
        points > 5000 -> "CITIZEN"
        else -> "NEWCOMER"
    }

    PremiumCardBox(
        modifier = modifier.height(60.dp),
        backgroundColor = SurfaceLayer.copy(alpha = 0.5f),
        borderColor = GlassBorder
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "LIFESTYLE RANK",
                    style = Typography.labelSmall,
                    color = PremiumPurple.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = rank,
                    style = Typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = points.toString(),
                style = Typography.titleLarge,
                color = PremiumGold,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun DailyPulseTicker(
    text: String,
    modifier: Modifier = Modifier
) {
    PremiumCardBox(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        borderColor = Color.White.copy(alpha = 0.05f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "PULSE:",
                style = Typography.labelSmall,
                color = PremiumPink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = Spacing.sm)
            )
            Text(
                text = text,
                style = Typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 1,
                fontSize = 12.sp
            )
        }
    }
}
