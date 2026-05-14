package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

/**
 * Detailed emotional state of the pet.
 * Encapsulates primary mood, intensity, and active modifiers.
 */
@Serializable
data class EmotionState(
    val primaryMood: Mood = Mood.HAPPY,
    val intensity: Float = 0.5f, // 0.0 to 1.0
    val modifiers: List<MoodModifier> = emptyList(),
    val lastInteractionTimestamp: Long = System.currentTimeMillis()
)

/**
 * Temporary emotional shifts from specific events (e.g., feeding, petting).
 */
@Serializable
data class MoodModifier(
    val mood: Mood,
    val intensity: Float,
    val expirationTimestamp: Long,
    val source: String
)
