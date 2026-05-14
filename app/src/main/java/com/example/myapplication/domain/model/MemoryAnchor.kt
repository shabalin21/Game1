package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A persistent memory that anchors an emotion to an object, time, or event.
 */
@Serializable
data class MemoryAnchor(
    val id: String = UUID.randomUUID().toString(),
    val type: MemoryType,
    val sourceId: String, // e.g., Item ID or Event ID
    val intensity: Float,  // 0.0 to 1.0
    val emotionalValence: Float, // -1.0 (Traumatic) to 1.0 (Blissful)
    val timestamp: Long = System.currentTimeMillis(),
    val decayRate: Float = 0.01f // Per hour
)

@Serializable
enum class MemoryType {
    ITEM_INTERACTION,
    PLAYER_ABSENCE,
    ENVIRONMENTAL_EVENT,
    TRAUMATIC_STAT_CRASH,
    MILESTONE
}

/**
 * Tracks the "Soul" bonds with specific objects.
 */
@Serializable
data class ObjectBond(
    val itemId: String,
    val familiarity: Float = 0f,
    val attachment: Float = 0f,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
)
