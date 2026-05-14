package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

/**
 * The unified psychological state of the companion.
 * Represents "Continuity" rather than just a "Session".
 */
@Serializable
data class PsychologyState(
    val temperament: Temperament = Temperament.default(),
    val memories: List<MemoryAnchor> = emptyList(),
    val objectBonds: Map<String, ObjectBond> = emptyMap(),
    val currentDesires: List<Desire> = emptyList(),
    val currentActivity: ActivityType = ActivityType.IDLE,
    val stressLegacy: Float = 0f,
    val addictionLevel: Float = 0f, // 0-100
    val dopamineLevel: Float = 50f,  // 0-100
    val impulseControl: Float = 100f, // 0-100
    val emotionalStability: Float = 100f // 0-100
)

@Serializable
data class BuddyActivity(
    val type: ActivityType,
    val confidence: Float,
    val targetObjectId: String? = null
)

@Serializable
enum class ActivityType {
    IDLE,
    RESTING,
    LOOKING_AROUND,
    WANTING_ATTENTION,
    HIDING,
    PACING,
    PLAYING,
    GRUMPY
}

@Serializable
data class Desire(
    val type: DesireType,
    val intensity: Float, // 0.0 to 1.0
    val priority: Int = 0
)

@Serializable
enum class DesireType {
    SOLITUDE,
    STIMULATION,
    COMFORT,
    ROUTINE,
    CURIOSITY,
    AFFECTION
}
