package com.example.myapplication.ui.fx

import androidx.compose.ui.graphics.Color
import com.example.myapplication.domain.model.TimeOfDay
import com.example.myapplication.domain.model.Weather
import com.example.myapplication.domain.model.WorldState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AtmosphereManager @Inject constructor(
    private val particleEngine: ParticleEngine
) {
    fun getAmbientColor(worldState: WorldState): Color {
        return when (worldState.timeOfDay) {
            TimeOfDay.DAWN -> Color(0xFFFF9E80).copy(alpha = 0.1f)
            TimeOfDay.DAY -> Color.Transparent
            TimeOfDay.DUSK -> Color(0xFFFF5252).copy(alpha = 0.1f)
            TimeOfDay.NIGHT -> Color(0xFF303F9F).copy(alpha = 0.2f)
        }
    }

    fun update(deltaTime: Float, worldState: WorldState) {
        // Spawn environmental particles
        when (worldState.weather) {
            Weather.RAINY -> spawnRain(deltaTime)
            Weather.STORMY -> {
                spawnRain(deltaTime * 2f)
                // Maybe occasional lightning?
            }
            Weather.CLOUDY -> {}
            Weather.SUNNY -> spawnDust(deltaTime)
        }
    }

    private fun spawnRain(deltaTime: Float) {
        if (Math.random() < 0.2 * deltaTime * 60) {
            particleEngine.spawn(
                x = Math.random().toFloat(),
                y = -0.1f,
                vx = 0.05f,
                vy = 0.8f,
                life = 2f,
                color = Color(0xFF81D4FA),
                scale = 0.5f,
                type = ParticleType.RAIN
            )
        }
    }

    private fun spawnDust(deltaTime: Float) {
        if (Math.random() < 0.05 * deltaTime * 60) {
            particleEngine.spawn(
                x = Math.random().toFloat(),
                y = Math.random().toFloat(),
                vx = (Math.random().toFloat() - 0.5f) * 0.02f,
                vy = (Math.random().toFloat() - 0.5f) * 0.02f,
                life = 5f,
                color = Color.White.copy(alpha = 0.2f),
                scale = 0.3f,
                type = ParticleType.DUST
            )
        }
    }
}
