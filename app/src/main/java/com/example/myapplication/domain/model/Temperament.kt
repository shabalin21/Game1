package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

/**
 * Biased Personality Vectors.
 * These are static or slow-changing values that bias the creature's 
 * autonomous behavior and stat decay.
 */
@Serializable
data class Temperament(
    val independence: Float = 0.5f, // 0.0 (Clingy) to 1.0 (Lone Wolf)
    val curiosity: Float = 0.5f,    // 0.0 (Cautious) to 1.0 (Adventurous)
    val affection: Float = 0.5f,    // 0.0 (Aloof) to 1.0 (Warm)
    val laziness: Float = 0.5f,     // 0.0 (Energetic) to 1.0 (Sluggish)
    val resilience: Float = 0.5f    // 0.0 (Fragile) to 1.0 (Tough)
) {
    companion object {
        fun default() = Temperament()
        
        fun random() = Temperament(
            independence = (0..100).random() / 100f,
            curiosity = (0..100).random() / 100f,
            affection = (0..100).random() / 100f,
            laziness = (0..100).random() / 100f,
            resilience = (0..100).random() / 100f
        )
    }
}
