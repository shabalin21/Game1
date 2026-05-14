package com.example.myapplication.ui.animation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.*
import com.example.myapplication.domain.model.Mood
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.myapplication.ui.fx.ParticleEngine
import com.example.myapplication.ui.fx.ParticleType as FxParticleType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PRODUCTION-GRADE JUICE MANAGER.
 * Manages transient effects like particles, haptics, and screen shakes.
 * Refactored to delegate to ParticleEngine.
 */
@Singleton
class JuiceManager @Inject constructor(
    val particleEngine: ParticleEngine
) {

    private val _juiceEvents = MutableSharedFlow<JuiceEvent>(extraBufferCapacity = 16)
    val juiceEvents = _juiceEvents.asSharedFlow()

    suspend fun triggerEffect(event: JuiceEvent) = withContext(Dispatchers.Main) {
        _juiceEvents.emit(event)
        
        when (event) {
            is JuiceEvent.MoodChanged -> spawnMoodParticles(event.newMood)
            is JuiceEvent.Interaction -> spawnInteractionParticles(event.type)
        }
    }

    private fun spawnMoodParticles(mood: Mood) {
        val color = when(mood) {
            Mood.HAPPY -> PremiumGold
            Mood.SAD -> PremiumBlue
            Mood.ANGRY -> PremiumRed
            Mood.EXCITED -> PremiumPink
            Mood.SLEEPY -> PremiumBlue.copy(alpha = 0.5f)
            else -> Color.White
        }
        
        particleEngine.spawnExplosion(0.5f, 0.4f, color, count = 12)
    }

    private fun spawnInteractionParticles(type: InteractionType) {
        val (color, pType) = when(type) {
            InteractionType.FEED -> PremiumGold to FxParticleType.SPARKLE
            InteractionType.PET -> PremiumPink to FxParticleType.HEART
            InteractionType.SLEEP -> PremiumBlue to FxParticleType.ZZZ
            else -> PremiumPurple to FxParticleType.SPARKLE
        }
        
        repeat(6) {
            particleEngine.spawn(
                x = 0.5f, y = 0.4f,
                vx = (Math.random().toFloat() - 0.5f) * 0.12f,
                vy = -0.08f - Math.random().toFloat() * 0.12f,
                life = 1.2f,
                color = color.copy(alpha = 0.6f),
                type = pType
            )
        }
    }

    // Deprecated: JuiceOverlay now calls particleEngine.update directly via its own loop or shared clock
    fun updateParticles(deltaTime: Float) {
        particleEngine.update(deltaTime)
    }
}

sealed class JuiceEvent {
    data class MoodChanged(val newMood: Mood) : JuiceEvent()
    data class Interaction(val type: InteractionType) : JuiceEvent()
}

enum class InteractionType {
    FEED, PET, PLAY, SLEEP
}

data class ParticleInstance(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val color: androidx.compose.ui.graphics.Color,
    val type: ParticleType
)

enum class ParticleType {
    SPARKLE, SMOKE, HEART, ZZZ
}
