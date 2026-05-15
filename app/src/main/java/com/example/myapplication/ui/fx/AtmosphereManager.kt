package com.example.myapplication.ui.fx

import androidx.compose.ui.graphics.Color
import com.example.myapplication.domain.simulation.atmosphere.AtmosphereState
import com.example.myapplication.domain.simulation.atmosphere.UiTone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AtmosphereManager @Inject constructor(
    private val particleEngine: ParticleEngine
) {
    fun getAmbientColor(state: AtmosphereState): Color {
        return when (state.uiTone) {
            UiTone.NEUTRAL -> Color.Transparent
            UiTone.WARM -> Color(0xFFFF9E80).copy(alpha = 0.1f)
            UiTone.COLD -> Color(0xFF303F9F).copy(alpha = 0.2f)
            UiTone.MELANCHOLIC -> Color(0xFF546E7A).copy(alpha = 0.15f)
            UiTone.ANXIOUS -> Color(0xFFFF5252).copy(alpha = 0.1f)
            UiTone.EUPHORIC -> Color(0xFFFFD700).copy(alpha = 0.1f)
        }
    }

    fun update(deltaTime: Float, state: AtmosphereState, isSleeping: Boolean = false) {
        // Logic based on state.activeAmbientVfx
        if (state.audioLayers.contains("rain_ambient")) {
            spawnRain(deltaTime)
        }
        
        if (isSleeping) {
            spawnSleep(deltaTime)
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

    private fun spawnSleep(deltaTime: Float) {
        if (Math.random() < 0.05 * deltaTime * 60) {
            particleEngine.spawn(
                x = 0.5f + (Math.random().toFloat() - 0.5f) * 0.2f, // Near center
                y = 0.4f,
                vx = 0.02f,
                vy = -0.05f,
                life = 3f,
                color = Color(0xFF90CAF9),
                scale = 0.6f,
                type = ParticleType.ZZZ
            )
        }
    }
}
