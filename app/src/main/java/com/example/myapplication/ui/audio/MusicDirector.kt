package com.example.myapplication.ui.audio

import com.example.myapplication.ui.render.RenderState
import com.example.myapplication.domain.model.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicDirector @Inject constructor(
    private val audioManager: AudioManager
) {
    fun update(state: RenderState) {
        // Task 7: Adaptive music intensity
        val stressLevel = state.stats.stress / 100f
        val energyLevel = state.stats.energy / 100f
        
        // Intensity layer based on stress
        if (stressLevel > 0.6f) {
            audioManager.setLayer("stress_layer", 0, (stressLevel - 0.6f) * 2f) // resId placeholder
        } else {
            audioManager.stopLayer("stress_layer")
        }

        // Nighttime theme
        if (state.world.timeLabel == "NIGHT") {
            audioManager.setLayer("night_theme", 0, 0.5f)
            audioManager.stopLayer("day_theme")
        } else {
            audioManager.setLayer("day_theme", 0, 0.5f)
            audioManager.stopLayer("night_theme")
        }

        // Weather layers
        if (state.atmosphere.audioLayers.contains("rain_ambient")) {
            audioManager.setLayer("rain_ambient", 0, 0.3f)
        } else {
            audioManager.stopLayer("rain_ambient")
        }

        // Financial pressure (loneliness/stress combined for now as a proxy)
        if (state.coins < 100 && energyLevel < 0.3f) {
            audioManager.setLayer("pressure_layer", 0, 0.4f)
        } else {
            audioManager.stopLayer("pressure_layer")
        }
    }
}
