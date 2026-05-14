package com.example.myapplication.ui.debug.module

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.fx.ParticleEngine
import com.example.myapplication.ui.fx.ParticleType
import com.example.myapplication.ui.theme.NeonPurple

@Composable
fun VFXInspectorModule(
    particleEngine: ParticleEngine
) {
    val activeCount = particleEngine.activeParticles.size
    val maxCount = particleEngine.maxParticles

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            "PARTICLE DIAGNOSTICS",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Active Particles", color = Color.White, fontSize = 14.sp)
            Text(
                "$activeCount / $maxCount",
                color = if (activeCount > maxCount * 0.8) Color.Red else Color.Cyan,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }

        LinearProgressIndicator(
            progress = activeCount.toFloat() / maxCount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = NeonPurple,
            trackColor = Color.White.copy(alpha = 0.1f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "STRESS TESTING",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                repeat(500) {
                    particleEngine.spawn(
                        x = (100..900).random().toFloat(),
                        y = (100..1800).random().toFloat(),
                        vx = (-5..5).random().toFloat(),
                        vy = (-5..5).random().toFloat(),
                        life = 2.0f,
                        color = Color.White,
                        scale = 1.0f,
                        type = ParticleType.SPARKLE
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF442222))
        ) {
            Text("SPAWN 500 PARTICLES", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                particleEngine.spawnExplosion(500f, 1000f, Color.Magenta, 100)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("TEST EXPLOSION FX", fontWeight = FontWeight.Bold)
        }
    }
}
