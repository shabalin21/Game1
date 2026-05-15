package com.example.myapplication.ui.screen.debug

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.debug.DevLabManager
import com.example.myapplication.ui.theme.*
import com.example.myapplication.ui.component.GlassCard

@Composable
fun DebugMenu(
    onBack: () -> Unit,
    devLabManager: DevLabManager
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red.copy(alpha = 0.1f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = PremiumPink)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "INTERNAL_DIAGNOSTICS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = PremiumPink,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        "UNAUTHORIZED ACCESS DETECTED",
                        style = MaterialTheme.typography.labelSmall,
                        color = PremiumPink.copy(alpha = 0.5f),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // PLAYER CONTROL
                item {
                    DebugSection("RESOURCES", Icons.Default.ShoppingCart) {
                        DebugActionButton("ADD 10,000 CREDITS") { /* Handled via repo in proper impl */ }
                        DebugToggleRow("INFINITE COINS", devLabManager.infiniteCoins) { devLabManager.updateVisualToggle("infiniteCoins", it) }
                        DebugActionButton("MAX LEVEL") { devLabManager.addXp(999999) }
                    }
                }

                // STAT CONTROL
                item {
                    DebugSection("VITAL_SIGNS", Icons.Default.Favorite) {
                        DebugToggleRow("GOD MODE", devLabManager.godModeEnabled) { devLabManager.toggleGodMode() }
                        DebugToggleRow("PAUSE SIMULATION", devLabManager.simulationPaused) { devLabManager.toggleSimulationPause() }
                        
                        Text("TIME DILATION: ${"%.1f".format(devLabManager.timeDilation)}x", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        Slider(
                            value = devLabManager.timeDilation,
                            onValueChange = { devLabManager.setTimeDilationValue(it) },
                            valueRange = 0.1f..10f,
                            colors = SliderDefaults.colors(activeTrackColor = PremiumPink)
                        )
                    }
                }

                // WORK CONTROL
                item {
                    DebugSection("EMPLOYMENT", Icons.Default.Build) {
                        DebugToggleRow("AUTO PERFORMANCE", devLabManager.autoMaintainPerformance) { devLabManager.updateVisualToggle("autoPerformance", it) }
                        DebugToggleRow("DISABLE FIRING", devLabManager.disableFiring) { devLabManager.updateVisualToggle("disableFiring", it) }
                    }
                }

                // VISUALS
                item {
                    DebugSection("RENDER_PIPELINE", Icons.Default.Refresh) {
                        DebugToggleRow("SHOW FPS", devLabManager.showFpsCounter) { devLabManager.updateVisualToggle("fps", it) }
                        DebugToggleRow("SHOW MEMORY", devLabManager.showMemoryUsage) { devLabManager.updateVisualToggle("memory", it) }
                        DebugToggleRow("PARTICLES", devLabManager.particlesEnabled) { devLabManager.updateVisualToggle("particles", it) }
                        DebugToggleRow("GLOW", devLabManager.glowEnabled) { devLabManager.updateVisualToggle("glow", it) }
                    }
                }

                // LOGS
                item {
                    DebugSection("SYSTEM_LOGS", Icons.AutoMirrored.Filled.List) {
                        val logs by devLabManager.debugLogs.collectAsState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            LazyColumn {
                                items(logs) { log ->
                                    Text(
                                        text = log,
                                        color = PremiumGreen,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DebugSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = PremiumPink, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = PremiumPink, fontWeight = FontWeight.Black, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = PremiumPink.copy(alpha = 0.5f)) {
            content()
        }
    }
}

@Composable
fun DebugToggleRow(label: String, value: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Switch(
            checked = value,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PremiumPink
            )
        )
    }
}

@Composable
fun DebugActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PremiumPink.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, color = PremiumPink, fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}

