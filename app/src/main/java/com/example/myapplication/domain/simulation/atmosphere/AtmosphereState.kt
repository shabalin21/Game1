package com.example.myapplication.domain.simulation.atmosphere

import kotlinx.serialization.Serializable

@Serializable
data class AtmosphereState(
    val uiTone: UiTone = UiTone.NEUTRAL,
    val blurIntensity: Float = 0f,
    val lightingOverlayAlpha: Float = 0f,
    val vignetteIntensity: Float = 0f,
    val grainIntensity: Float = 0.05f,
    val saturation: Float = 1.0f,
    val brightness: Float = 1.0f,
    val activeAmbientVfx: List<String> = emptyList(),
    val audioLayers: List<String> = emptyList()
)

@Serializable
enum class UiTone {
    NEUTRAL,
    WARM,
    COLD,
    MELANCHOLIC,
    ANXIOUS,
    EUPHORIC
}
