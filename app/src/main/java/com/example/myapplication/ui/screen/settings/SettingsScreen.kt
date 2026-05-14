package com.example.myapplication.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToDebug: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PremiumBackground(accentColor = PremiumPurple) {}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(
                title = "Settings",
                subtitle = "System Preferences",
                accentColor = PremiumPurple,
                onBack = onBack
            )

            // Visual Settings
            SettingsSection("VISUALS") {
                SettingToggle("Particle Systems", settings.graphics.particlesEnabled) { 
                    viewModel.updateGraphics(settings.graphics.copy(particlesEnabled = it)) 
                }
                SettingToggle("Dynamic Effects", settings.graphics.dynamicBackgrounds) { 
                    viewModel.updateGraphics(settings.graphics.copy(dynamicBackgrounds = it)) 
                }
                
                // FPS Control
                SettingToggle("Show FPS Counter", settings.graphics.showFps) {
                    viewModel.updateGraphics(settings.graphics.copy(showFps = it))
                }

                FPSSelector(
                    currentFps = settings.graphics.targetFps,
                    onFpsChange = { viewModel.updateGraphics(settings.graphics.copy(targetFps = it)) }
                )

                SettingToggle("Low Power Mode", settings.graphics.lowPowerMode) { 
                    viewModel.updateGraphics(settings.graphics.copy(lowPowerMode = it)) 
                }
            }

            // Audio Settings
            SettingsSection("AUDIO") {
                VolumeSlider("Master Volume", settings.audio.masterVolume) {
                    viewModel.updateAudio(settings.audio.copy(masterVolume = it))
                }
                VolumeSlider("Music Volume", settings.audio.musicVolume) {
                    viewModel.updateAudio(settings.audio.copy(musicVolume = it))
                }
                VolumeSlider("Effects Volume", settings.audio.sfxVolume) {
                    viewModel.updateAudio(settings.audio.copy(sfxVolume = it))
                }
                SettingToggle("Mute All", settings.audio.isMuted) {
                    viewModel.updateAudio(settings.audio.copy(isMuted = it))
                }
            }

            // Notification Settings
            SettingsSection("NOTIFICATIONS") {
                SettingToggle("Push Notifications", settings.gameplay.notificationsEnabled) { 
                    viewModel.updateGameplay(settings.gameplay.copy(notificationsEnabled = it)) 
                }
            }

            // Danger Zone
            var showResetDialog by remember { mutableStateOf(false) }
            
            SettingsSection("DANGER ZONE") {
                Text(
                    "Irreversible system actions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                PremiumButton(
                    text = "Reset Game Data",
                    onClick = { showResetDialog = true },
                    accentColor = PremiumRed,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = { Text("Factory Reset") },
                    text = { Text("This will permanently delete your pet, coins, and all progress. This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.resetSave()
                                showResetDialog = false
                                onBack() // Navigate back after reset
                            }
                        ) {
                            Text("RESET", color = PremiumRed, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetDialog = false }) {
                            Text("CANCEL")
                        }
                    },
                    containerColor = SurfaceDark,
                    titleContentColor = Color.White,
                    textContentColor = Color.White.copy(alpha = 0.7f)
                )
            }

            // System Info
            SettingsSection("SYSTEM INFO") {
                InfoRow("Version", "v2.5.0-PREMIUM")
                InfoRow("Status", "STABLE")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PremiumButton(
                    text = "Sync Account",
                    onClick = { /* Future sync logic */ },
                    accentColor = PremiumPurple,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TextButton(
                onClick = onNavigateToDebug,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Developer Settings", style = MaterialTheme.typography.labelSmall, color = PremiumRed.copy(alpha = 0.4f))
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun VolumeSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = PremiumPurple)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = PremiumPurple,
                activeTrackColor = PremiumPurple,
                inactiveTrackColor = PremiumPurple.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = Color.White)
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange, 
            colors = SwitchDefaults.colors(
                checkedThumbColor = PremiumPurple, 
                checkedTrackColor = PremiumPurple.copy(alpha = 0.2f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.White.copy(alpha = 0.05f)
            )
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
        Text(value, style = MaterialTheme.typography.labelSmall, color = PremiumPurple, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FPSSelector(currentFps: Int, onFpsChange: (Int) -> Unit) {
    Column {
        Text("TARGET FRAMERATE", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(30, 60, 120).forEach { fps ->
                val isSelected = currentFps == fps
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(Shapes.buttonRadius))
                        .background(if (isSelected) PremiumPurple.copy(alpha = 0.15f) else GlassBackground)
                        .border(1.dp, if (isSelected) PremiumPurple else GlassBorder, RoundedCornerShape(Shapes.buttonRadius))
                        .clickable { onFpsChange(fps) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${fps} FPS", style = MaterialTheme.typography.labelSmall, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}
