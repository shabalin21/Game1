package com.example.myapplication.ui.debug.module

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.debug.DevLabManager
import com.example.myapplication.ui.theme.PremiumPurple

@Composable
fun VisualsModule(manager: DevLabManager) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        VisualToggle("Particles", manager.particlesEnabled) { manager.updateVisualToggle("particles", it) }
        VisualToggle("Ambient Glow", manager.glowEnabled) { manager.updateVisualToggle("glow", it) }
        VisualToggle("Animations", manager.animationsEnabled) { manager.updateVisualToggle("animations", it) }
        VisualToggle("UI Blur", manager.blurEnabled) { manager.updateVisualToggle("blur", it) }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
        
        VisualToggle("FPS Counter", manager.showFpsCounter) { manager.updateVisualToggle("fps", it) }
        VisualToggle("Memory Usage", manager.showMemoryUsage) { manager.updateVisualToggle("memory", it) }
    }
}

@Composable
private fun VisualToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PremiumPurple,
                checkedTrackColor = PremiumPurple.copy(alpha = 0.5f)
            )
        )
    }
}

