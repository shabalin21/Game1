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
    val particles = particleEngine.activeParticles
    val targetFps by viewModel.targetFps.collectAsState()
    val weather by viewModel.weather.collectAsState()
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
                
                particleEngine.update(deltaTime)
                
                // Ambient Spawning
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
        // Redraw on every frame trigger
        val _trigger = frameTrigger

        particles.forEach { particle ->
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
                ParticleType.HEART -> {
                    // Draw a simple heart shape or circle for now
                    drawCircle(color = color, radius = radius, center = center)
                }
                ParticleType.SPARKLE -> {
                    drawCircle(color = color, radius = radius, center = center)
                }
                ParticleType.ZZZ -> {
                    drawCircle(color = color, radius = radius, center = center)
                }
                ParticleType.SMOKE -> {
                    drawCircle(color = color.copy(alpha = alpha * 0.3f), radius = radius, center = center)
                }
                ParticleType.RAIN -> {
                    drawLine(
                        color = color,
                        start = center,
                        end = center + Offset(2f, 10f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                ParticleType.DUST -> {
                    drawCircle(color = color, radius = radius, center = center)
                }
                else -> {
                    drawCircle(color = color, radius = radius, center = center)
                }
            }
        }
    }
}
