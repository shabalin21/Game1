package com.example.myapplication.ui.debug.module

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.debug.DevLabManager
import com.example.myapplication.ui.theme.NeonPurple

import java.util.Locale

@Composable
fun SimulationControlModule(
    manager: DevLabManager,
    onTickManual: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            "TIME MANIPULATION",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Simulation Paused", color = Color.White, fontSize = 14.sp)
            Switch(
                checked = manager.simulationPaused,
                onCheckedChange = { manager.toggleSimulationPause() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonPurple,
                    checkedTrackColor = NeonPurple.copy(alpha = 0.5f)
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("God Mode", color = Color.White, fontSize = 14.sp)
            Switch(
                checked = manager.godModeEnabled,
                onCheckedChange = { manager.toggleGodMode() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonPurple,
                    checkedTrackColor = NeonPurple.copy(alpha = 0.5f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Time Dilation (x${String.format(Locale.US, "%.1f", manager.timeDilation)})",
            color = Color.White,
            fontSize = 14.sp
        )
        Slider(
            value = manager.timeDilation,
            onValueChange = { manager.setTimeDilationValue(it) },
            valueRange = 1f..10f,
            colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "MANUAL TICKS",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TickButton("1H", onClick = { onTickManual(1) })
            TickButton("6H", onClick = { onTickManual(6) })
            TickButton("12H", onClick = { onTickManual(12) })
            TickButton("24H", onClick = { onTickManual(24) })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { onTickManual(168) }, // 1 Week
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("ADVANCE 1 WEEK", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun TickButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.width(65.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
