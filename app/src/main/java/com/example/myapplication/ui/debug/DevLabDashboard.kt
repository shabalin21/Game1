package com.example.myapplication.ui.debug

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.PetModel
import com.example.myapplication.ui.fx.ParticleEngine
import com.example.myapplication.ui.theme.*

@Composable
fun DevLabDashboard(
    manager: DevLabManager,
    pet: PetModel,
    coins: Int,
    particleEngine: ParticleEngine,
    onUpdateStat: (String, Float) -> Unit,
    onTickManual: (Float) -> Unit,
    onCommand: (String) -> Unit,
    onAddCoins: (Int) -> Unit
) {
    if (!manager.isVisible) return

    val debugLogs by manager.debugLogs.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Developer Console",
                    color = NeonGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { manager.isVisible = false }) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = NeonGreen)
                }
            }

            Divider(color = NeonGreen.copy(alpha = 0.3f), thickness = 1.dp)
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxSize()) {
                // LEFT COLUMN: CONTROLS
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item { DebugSectionHeader("Resources") }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                DebugActionButton("Add Coins") { onAddCoins(1000) }
                                DebugActionButton("Level Up") { onCommand("/levelup") }
                            }
                        }
                        
                        item { DebugSectionHeader("Stats") }
                        item {
                            DebugToggle("God Mode", manager.godModeEnabled) { manager.toggleGodMode() }
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                DebugActionButton("Max Hunger") { onUpdateStat("hunger", 100f) }
                                DebugActionButton("Max Energy") { onUpdateStat("energy", 100f) }
                            }
                        }

                        item { DebugSectionHeader("Time Control") }
                        item {
                            DebugToggle("Pause Sim", manager.simulationPaused) { manager.toggleSimulationPause() }
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                DebugActionButton("Skip 1H") { onTickManual(1.0f) }
                                DebugActionButton("Skip 6H") { onTickManual(6.0f) }
                            }
                        }

                        item { DebugSectionHeader("Performance") }
                        item {
                            DebugToggle("Show FPS", manager.showPerformanceOverlay) {
                                manager.updateVisualToggle("performance", !manager.showPerformanceOverlay)
                            }
                        }
                        item {
                            DebugToggle("Auto Performance", manager.autoMaintainPerformance) { 
                                manager.updateVisualToggle("autoPerformance", !manager.autoMaintainPerformance) 
                            }
                        }
                        item {
                            DebugToggle("Disable Firing", manager.disableFiring) { 
                                manager.updateVisualToggle("disableFiring", !manager.disableFiring) 
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT COLUMN: LOGS
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Text(
                        text = "System Logs:",
                        color = NeonGreen.copy(alpha = 0.6f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp)
                    ) {
                        LazyColumn {
                            items(debugLogs) { log ->
                                Text(
                                    text = log,
                                    color = NeonGreen,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DebugSectionHeader(label: String) {
    Text(
        text = "[$label]",
        color = NeonGreen.copy(alpha = 0.6f),
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun DebugActionButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(NeonGreen.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = NeonGreen,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DebugToggle(label: String, enabled: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .border(1.dp, NeonGreen, RoundedCornerShape(2.dp))
                .background(if (enabled) NeonGreen else Color.Transparent)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = if (enabled) NeonGreen else NeonGreen.copy(alpha = 0.4f),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )
    }
}
