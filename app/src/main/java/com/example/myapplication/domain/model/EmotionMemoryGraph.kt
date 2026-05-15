package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * FOUNDATION FOR THE PERSISTENT EMOTIONAL MEMORY-GRAPH SYSTEM.
 * Represents memories as nodes and associations as edges.
 */
@Serializable
data class EmotionMemoryGraph(
    val nodes: Map<String, MemoryNode> = emptyMap(),
    val edges: List<MemoryAssociation> = emptyList()
)

@Serializable
data class MemoryNode(
    val id: String = UUID.randomUUID().toString(),
    val type: MemoryType,
    val sourceId: String,
    val valence: Float, // -1.0 (Traumatic) to 1.0 (Blissful)
    val baseIntensity: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class MemoryAssociation(
    val fromNodeId: String,
    val toNodeId: String,
    val strength: Float, // 0.0 to 1.0
    val associationType: AssociationType = AssociationType.TEMPORAL
)

@Serializable
enum class AssociationType {
    TEMPORAL, // Occurred around the same time
    CAUSAL,   // One caused the other
    SEMANTIC  // Related by content/category
}

/**
 * Helper to calculate current emotional weight of a memory based on decay.
 */
fun MemoryNode.getCurrentWeight(currentTime: Long): Float {
    val ageHours = (currentTime - timestamp).toDouble() / (1000.0 * 60.0 * 60.0)
    val decayRate = 0.05 // 5% per hour
    return (baseIntensity * Math.exp(-decayRate * ageHours)).toFloat().coerceIn(0f, 1f)
}
