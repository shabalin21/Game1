package com.example.myapplication.ui.screen.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.MissionProgress
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.screen.home.PetViewModel
import com.example.myapplication.ui.theme.*

@Composable
fun MissionsScreen(
    viewModel: PetViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val pet by viewModel.petState.collectAsState()
    val missions = pet?.missions ?: com.example.myapplication.domain.model.MissionState()

    Box(modifier = Modifier.fillMaxSize()) {
        CyberBackground(accentColor = PremiumBlue)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            ScreenHeader(
                title = "OBJECTIVES",
                subtitle = "DAILY_WEEKLY_SYSTEM",
                accentColor = PremiumBlue,
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item { MissionHeader("DAILY MISSIONS", PremiumGreen) }
                items(missions.dailyMissions) { mission ->
                    MissionCard(mission)
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                item { MissionHeader("WEEKLY MISSIONS", PremiumGold) }
                items(missions.weeklyMissions) { mission ->
                    MissionCard(mission)
                }
            }
        }
    }
}

@Composable
fun MissionHeader(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = color.copy(alpha = 0.6f),
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun MissionCard(mission: MissionProgress) {
    CyberCard(accentColor = if (mission.isCompleted) PremiumGreen else Color.White.copy(alpha = 0.1f)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(mission.title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                if (mission.isCompleted) {
                    CyberBadge(text = "COMPLETED", accentColor = PremiumGreen)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(mission.description, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val progress = (mission.currentProgress / mission.targetGoal).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = if (mission.isCompleted) PremiumGreen else PremiumBlue,
                trackColor = Color.White.copy(alpha = 0.05f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${mission.currentProgress.toInt()} / ${mission.targetGoal.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f)
                )
                Text(
                    "Reward: ${mission.reward.coins} CR",
                    style = MaterialTheme.typography.labelSmall,
                    color = PremiumGold,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

