package com.example.myapplication.ui.screen.work

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.work.model.*
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*

import java.util.Locale

@Composable
fun WorkScreen(
    viewModel: WorkViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val pet by viewModel.petState.collectAsState()
    val career by viewModel.careerState.collectAsState()
    val marketTrends by viewModel.marketTrends.collectAsState()
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PremiumBackground(accentColor = PremiumGreen) {}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = "Career",
                subtitle = "Professional Progression",
                accentColor = PremiumGreen,
                onBack = onBack,
                trailingContent = {
                    GlassCard(modifier = Modifier.height(52.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                            Text("TOTAL EARNED", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                            Text("${career.totalEarned} CR", style = MaterialTheme.typography.titleMedium, color = PremiumGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Work Efficiency",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
                NeonBadge(
                    text = "${career.currentEfficiency.toInt()}%",
                    accentColor = if (career.currentEfficiency > 80f) PremiumGreen else PremiumGold
                )
            }
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Active Job Section
                career.activeFullTimeJobId?.let { id ->
                    val job = viewModel.availableJobs.find { it.id == id }
                    if (job != null) {
                        item {
                            ActiveJobCard(
                                job = job,
                                career = career,
                                trend = marketTrends[job.id] ?: 1.0f,
                                onWork = { viewModel.work(it) },
                                onPromote = { viewModel.promote() }
                            )
                        }
                    }
                }
                
                // Job List Label
                item {
                    Text(
                        "LABOR MARKET",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Available Jobs
                items(viewModel.availableJobs) { job ->
                    val isActive = job.id == career.activeFullTimeJobId || career.activePartTimeJobIds.contains(job.id)
                    if (!isActive) {
                        JobOfferCard(
                            job = job,
                            trend = marketTrends[job.id] ?: 1.0f,
                            onApply = { viewModel.applyForJob(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveJobCard(job: Job, career: PlayerCareer, trend: Float, onWork: (String) -> Unit, onPromote: () -> Unit) {
    GlassCard(borderColor = PremiumGreen, showGlow = career.promotionEligibility) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(job.title, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text(job.company, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    NeonBadge("Active", accentColor = PremiumGreen)
                    MarketTrendBadge(trend)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatInfo("SALARY", "${(job.baseSalary * trend).toInt()} CR", PremiumGold)
                StatInfo("STRESS", "${(job.stressLevel * 100).toInt()}%", PremiumRed)
                StatInfo("EXPERIENCE", "${career.jobExperience[job.id] ?: 0} Days", PremiumBlue)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PremiumButton(text = "Start Shift", onClick = { onWork(job.id) }, accentColor = PremiumGreen, modifier = Modifier.weight(1f))
                if (career.promotionEligibility) {
                    PremiumButton(text = "Promote", onClick = onPromote, accentColor = PremiumPurple, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun JobOfferCard(job: Job, trend: Float, onApply: (Job) -> Unit) {
    GlassCard {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(job.title, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text(job.company, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    NeonBadge(if (job.type == JobType.FULL_TIME) "Full-Time" else "Part-Time", accentColor = PremiumBlue)
                    MarketTrendBadge(trend)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(job.description, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatInfo("SALARY", "${(job.baseSalary * trend).toInt()} CR", PremiumGold)
                    StatInfo("STRESS", "${(job.stressLevel * 100).toInt()}%", PremiumRed)
                }
                PremiumButton(text = "Apply", onClick = { onApply(job) }, accentColor = PremiumPurple)
            }
        }
    }
}

@Composable
fun MarketTrendBadge(trend: Float) {
    val color = when {
        trend > 1.2f -> PremiumGreen
        trend < 0.8f -> PremiumRed
        else -> Color.White.copy(alpha = 0.4f)
    }
    val icon = when {
        trend > 1.2f -> "▲"
        trend < 0.8f -> "▼"
        else -> "▶"
    }
    
    Text(
        text = "$icon ${String.format(Locale.US, "%.1fx", trend)}",
        color = color,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun StatInfo(label: String, value: String, color: Color) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
