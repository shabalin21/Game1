package com.example.myapplication.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.fx.ParticleEngine
import com.example.myapplication.ui.fx.ParticleType
import com.example.myapplication.domain.model.Weather
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myapplication.ui.animation.JuiceViewModel
import com.example.myapplication.util.fps.FpsLimiter

@Composable
fun JuiceOverlay(
    viewModel: JuiceViewModel,
    modifier: Modifier = Modifier
) {
    val particleEngine = viewModel.juiceManager.particleEngine
    val targetFps by viewModel.targetFps.collectAsState()
    val weather by viewModel.weather.collectAsState()
    
    // We use a state that updates on every frame to trigger the Canvas redraw.
    // However, we only update the particle logic inside withFrameNanos to stay in sync with the display.
    var frameTrigger by remember { mutableLongStateOf(0L) }

    // Update Loop
    LaunchedEffect(targetFps, weather) {
        var lastTimeNanos = 0L
        var spawnAccumulator = 0f

        while (isActive) {
            withFrameNanos { currentTimeNanos ->
                frameTrigger = currentTimeNanos
                
                if (lastTimeNanos == 0L) {
                    lastTimeNanos = currentTimeNanos
                    return@withFrameNanos
                }
                
                val deltaTime = (currentTimeNanos - lastTimeNanos) / 1_000_000_000f
                lastTimeNanos = currentTimeNanos
                
                // Authoritative particle update
                particleEngine.update(deltaTime)
                
                // Ambient Spawning logic (kept simple for performance)
                spawnAccumulator += deltaTime
                if (spawnAccumulator > 0.1f) {
                    spawnAccumulator = 0f
                    when (weather) {
                        Weather.RAINY, Weather.STORMY -> {
                            repeat(if (weather == Weather.STORMY) 5 else 2) {
                                particleEngine.spawn(
                                    x = Math.random().toFloat(),
                                    y = -0.1f,
                                    vx = 0.05f,
                                    vy = 0.8f + Math.random().toFloat() * 0.4f,
                                    life = 1.5f,
                                    color = PremiumBlue.copy(alpha = 0.5f),
                                    type = ParticleType.RAIN
                                )
                            }
                        }
                        Weather.SUNNY -> {
                            if (Math.random() < 0.2) {
                                particleEngine.spawn(
                                    x = Math.random().toFloat(),
                                    y = Math.random().toFloat(),
                                    vx = (Math.random().toFloat() - 0.5f) * 0.02f,
                                    vy = (Math.random().toFloat() - 0.5f) * 0.02f,
                                    life = 2f,
                                    color = PremiumGold.copy(alpha = 0.2f),
                                    type = ParticleType.DUST
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
            
            // Limit loop frequency if target is lower than typical refresh rates
            if (targetFps < 120) {
                val targetFrameTimeMillis = 1000L / targetFps
                delay(targetFrameTimeMillis)
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Access frameTrigger to ensure the Canvas is redrawn on every frame.
        // We use the non-state particles list from the engine for maximum performance.
        val _trigger = frameTrigger

        // Draw particles using a manual loop to avoid iterator allocation in the draw phase
        val activeParticles = particleEngine.particles
        for (i in 0 until activeParticles.size) {
            val particle = activeParticles[i]
            val center = Offset(particle.x * size.width, particle.y * size.height)
            val alpha = particle.alpha
            val color = particle.color.copy(alpha = alpha)
            val radius = particle.scale * when (particle.type) {
                ParticleType.RAIN -> 2f
                ParticleType.DUST -> 1f
                ParticleType.SMOKE -> 15f
                else -> 8f
            }.dp.toPx()
            
            when (particle.type) {
                ParticleType.RAIN -> {
                    drawLine(
                        color = color,
                        start = center,
                        end = center + Offset(2f, 10f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                else -> {
                    drawCircle(color = color, radius = radius, center = center)
                }
            }
        }
    }
}
