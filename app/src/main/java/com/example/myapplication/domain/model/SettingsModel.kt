package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SettingsModel(
    val graphics: GraphicsSettings = GraphicsSettings(),
    val audio: AudioSettings = AudioSettings(),
    val gameplay: GameplaySettings = GameplaySettings(),
    val ui: UiSettings = UiSettings()
)

@Serializable
data class GraphicsSettings(
    val showFps: Boolean = true,
    val targetFps: Int = 60, // 30, 60, 120
    val particlesEnabled: Boolean = true,
    val animationQuality: Int = 2, // 0: Low, 1: Med, 2: High
    val lowPowerMode: Boolean = false,
    val dynamicBackgrounds: Boolean = true,
    val shadowsEnabled: Boolean = true,
    val screenShakeEnabled: Boolean = true
)

@Serializable
data class AudioSettings(
    val masterVolume: Float = 0.8f,
    val musicVolume: Float = 0.7f,
    val sfxVolume: Float = 1.0f,
    val isMuted: Boolean = false,
    val ambientEnabled: Boolean = true
)

@Serializable
data class GameplaySettings(
    val difficulty: Int = 1, // 0: Easy, 1: Normal, 2: Hard
    val autoSleepEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val idleProgressionEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val tutorialEnabled: Boolean = true
)

@Serializable
data class UiSettings(
    val darkMode: Boolean = true,
    val compactUi: Boolean = false,
    val uiScale: Float = 1.0f,
    val floatingTextEnabled: Boolean = true,
    val statAnimationEnabled: Boolean = true
)
