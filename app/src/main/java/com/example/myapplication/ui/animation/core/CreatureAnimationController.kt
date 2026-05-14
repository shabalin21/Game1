package com.example.myapplication.ui.animation.core

import androidx.compose.runtime.*
import com.example.myapplication.domain.model.EmotionState
import com.example.myapplication.domain.model.Mood
import com.example.myapplication.domain.model.PsychologyState
import com.example.myapplication.domain.model.ActivityType
import kotlin.math.sin

/**
 * LAYERED ANIMATION CONTROLLER.
 * Manages procedural animations for the companion.
 */
@Stable
class CreatureAnimationController {
    // Procedure-based offsets
    var bodyScaleX by mutableFloatStateOf(1f)
    var bodyScaleY by mutableFloatStateOf(1f)
    var bodyOffsetY by mutableFloatStateOf(0f)
    var eyeScaleY by mutableFloatStateOf(1f)
    var jitterX by mutableFloatStateOf(0f)
    var eyeOffsetX by mutableFloatStateOf(0f)
    
    private var time = 0f
    private var reactionTime = 0f

    fun react() {
        reactionTime = 0.5f // Half a second of reaction
    }

    fun update(
        deltaTime: Float, 
        emotion: EmotionState, 
        psychology: PsychologyState,
        isSleeping: Boolean
    ) {
        // Clamp deltaTime to prevent huge jumps after app pauses/resumes
        val sanitizedDelta = deltaTime.coerceIn(0f, 0.1f)
        time += sanitizedDelta
        
        if (reactionTime > 0) {
            reactionTime -= sanitizedDelta
        }

        // Reset procedural values to prevent accumulation bugs (Fixes "Flying Buddy")
        jitterX = 0f
        eyeOffsetX = 0f
        bodyOffsetY = 0f

        // 1. ACTIVITY LAYER (Breathing)
        val breathingSpeed = when(psychology.currentActivity) {
            ActivityType.RESTING -> 1.2f
            ActivityType.PACING -> 5f
            ActivityType.HIDING -> 0.4f
            ActivityType.WANTING_ATTENTION -> 4f
            else -> 2.5f
        }
        
        val breath = sin(time * breathingSpeed)
        bodyScaleY = 1f + (breath * 0.02f)
        bodyScaleX = 1f - (breath * 0.015f)
        
        // 1.5 REACTION LAYER (Procedural Squash & Stretch)
        if (reactionTime > 0) {
            val reactionProgress = (reactionTime / 0.5f)
            bodyScaleY *= 1f - (reactionProgress * 0.2f)
            bodyScaleX *= 1f + (reactionProgress * 0.15f)
            bodyOffsetY += reactionProgress * 20f // Squash down
        }

        // 2. BEHAVIOR MODIFIERS
        when (psychology.currentActivity) {
            ActivityType.LOOKING_AROUND -> {
                eyeOffsetX = sin(time * 4f) * 4f
                bodyOffsetY = sin(time * 1.5f) * 4f
            }
            ActivityType.HIDING -> {
                bodyOffsetY = 25f // Lowered position
                bodyScaleY *= 0.85f
            }
            ActivityType.PACING -> {
                jitterX = sin(time * 3.5f) * 15f
                bodyOffsetY = Math.abs(sin(time * 5f)) * -6f // Slight hop while pacing
            }
            else -> {
                bodyOffsetY = sin(time * 1.5f) * 8f
            }
        }

        // 3. EMOTION LAYER
        if (emotion.primaryMood == Mood.ANGRY) {
            // Jitter should be based on time to be deterministic and reset each frame
            jitterX += sin(time * 40f) * 4f * emotion.intensity
        }
        
        if (emotion.primaryMood == Mood.HAPPY || emotion.primaryMood == Mood.EXCITED) {
            // Use time-based bounce instead of relative subtraction to prevent "flying"
            bodyOffsetY -= Math.abs(sin(time * 10f) * 12f) * emotion.intensity
        }

        // 4. EYE LAYER (Emotional Blinking)
        if (isSleeping || psychology.currentActivity == ActivityType.RESTING) {
            eyeScaleY = 0.1f
        } else {
            val blinkCycle = 5f
            val blinkProgress = (time % blinkCycle)
            
            eyeScaleY = when {
                blinkProgress > 4.85f -> 0.1f
                blinkProgress > 4.6f && blinkProgress < 4.7f -> 0.1f // Double blink
                else -> 1f
            }
        }

        // 5. BOUNDS SAFETY (Final Clamp)
        bodyOffsetY = bodyOffsetY.coerceIn(-100f, 100f)
        jitterX = jitterX.coerceIn(-50f, 50f)
    }
}

@Composable
fun rememberCreatureAnimationController(): CreatureAnimationController {
    return remember { CreatureAnimationController() }
}
