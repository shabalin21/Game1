package com.example.myapplication.ui.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.render.RenderState

@Composable
fun DevObservatory(
    state: RenderState,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SIMULATION OBSERVATORY", style = MaterialTheme.typography.headlineSmall, color = Color.Green)
                IconButton(onClick = onClose) {
                    Text("X", color = Color.White)
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            DebugSection("EMOTIONAL STATE") {
                DebugStat("Stress", state.stats.stress)
                DebugStat("Burnout", state.stats.burnout)
                DebugStat("Motivation", state.stats.motivation)
            }
            
            DebugSection("ATMOSPHERE") {
                DebugStat("Tone", state.atmosphere.uiTone.name)
                DebugStat("Blur", state.atmosphere.blurIntensity)
                DebugStat("Saturation", state.atmosphere.saturation)
            }
            
            DebugSection("ACTIVE MODIFIERS") {
                state.activeBuffs.forEach { buff ->
                    Text("${buff.label} (${(buff.progress * 100).toInt()}%)", color = Color.Cyan)
                }
            }
        }
    }
}

@Composable
fun DebugSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.Yellow, fontSize = 14.sp)
        HorizontalDivider(color = Color.DarkGray)
        Spacer(Modifier.height(4.dp))
        content()
    }
}

@Composable
fun DebugStat(label: String, value: Any) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.7f))
        Text(value.toString(), color = Color.White)
    }
}
