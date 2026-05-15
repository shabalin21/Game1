package com.example.myapplication.domain.simulation.atmosphere

import com.example.myapplication.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * THE ATMOSPHERE DIRECTOR.
 * Resolves environmental and emotional state into aesthetic parameters.
 */
@Singleton
class AtmosphereDirector @Inject constructor() {

    fun resolve(pet: PetModel, world: WorldState): AtmosphereState {
        val psychology = pet.psychology
        
        // 1. Determine UI Tone
        val uiTone = when {
            psychology.burnout > 70f -> UiTone.MELANCHOLIC
            psychology.stress > 60f -> UiTone.ANXIOUS
            world.timeOfDay == TimeOfDay.NIGHT -> UiTone.COLD
            world.weather == Weather.RAINY -> UiTone.MELANCHOLIC
            psychology.fulfillment > 80f -> UiTone.EUPHORIC
            else -> UiTone.NEUTRAL
        }

        // 2. Blur based on exhaustion/burnout
        val blur = if (pet.stats.energy < 20f || psychology.burnout > 80f) 5f else 0f

        // 3. Vignette based on stress/loneliness
        val vignette = (psychology.stress / 200f) + (psychology.loneliness / 200f)

        // 4. Saturation based on happiness
        val saturation = 0.5f + (pet.stats.happiness / 200f)

        // 5. Audio Layers
        val audioLayers = mutableListOf<String>()
        if (world.weather == Weather.RAINY) audioLayers.add("rain_ambient")
        if (world.weather == Weather.STORMY) audioLayers.add("thunder_low")
        if (psychology.loneliness > 60f) audioLayers.add("melancholic_pad")

        return AtmosphereState(
            uiTone = uiTone,
            blurIntensity = blur,
            vignetteIntensity = vignette,
            saturation = saturation,
            audioLayers = audioLayers
        )
    }
}
