package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

/**
 * Represents the 16 core stats of the creature simulation.
 * Values are normalized between 0.0 and 100.0.
 */
@Serializable
data class PetStats(
    val hunger: Float = 100f,
    val thirst: Float = 100f,
    val energy: Float = 100f,
    val hygiene: Float = 100f,
    val happiness: Float = 100f,
    val health: Float = 100f,
    val stress: Float = 0f,
    val social: Float = 50f,
    val intelligence: Float = 0f,
    val discipline: Float = 0f,
    val confidence: Float = 50f,
    val curiosity: Float = 50f,
    val comfort: Float = 100f,
    val fitness: Float = 50f,
    val attention: Float = 100f,
    val emotionalStability: Float = 50f
) {
    /**
     * Precision clamping. Ensures values stay within [0, 100].
     */
    fun clamped(): PetStats {
        fun f(v: Float) = v.takeIf { it.isFinite() }?.coerceIn(0f, 100f) ?: 0f
        return copy(
            hunger = f(hunger),
            thirst = f(thirst),
            energy = f(energy),
            hygiene = f(hygiene),
            happiness = f(happiness),
            health = f(health),
            stress = f(stress),
            social = f(social),
            intelligence = f(intelligence),
            discipline = f(discipline),
            confidence = f(confidence),
            curiosity = f(curiosity),
            comfort = f(comfort),
            fitness = f(fitness),
            attention = f(attention),
            emotionalStability = f(emotionalStability)
        )
    }

    // Helper for UI to get display integer safely. 
    // Uses roundToInt() to ensure 99.9f becomes 100.
    fun displayHunger() = (hunger.takeIf { it.isFinite() } ?: 0f).roundToInt().coerceIn(0, 100)
    fun displayEnergy() = (energy.takeIf { it.isFinite() } ?: 0f).roundToInt().coerceIn(0, 100)
    fun displayHappiness() = (happiness.takeIf { it.isFinite() } ?: 0f).roundToInt().coerceIn(0, 100)
    fun displayHealth() = (health.takeIf { it.isFinite() } ?: 0f).roundToInt().coerceIn(0, 100)
}
