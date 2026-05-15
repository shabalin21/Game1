package com.example.myapplication.domain.simulation

import com.example.myapplication.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PRODUCTION-GRADE MOOD ENGINE.
 * Handles state transitions, emotional decay, and organic reactivity.
 */
@Singleton
class MoodEngine @Inject constructor() {

    /**
     * Updates the emotional state based on current stats and time elapsed.
     */
    fun updateEmotionState(
        currentEmotion: EmotionState,
        stats: PetStats,
        psychology: PsychologyState,
        currentTimeMillis: Long,
        world: WorldState,
        memoryGraph: EmotionMemoryGraph = EmotionMemoryGraph()
    ): EmotionState {
        // 1. Decay/Expire temporary modifiers
        val activeModifiers = currentEmotion.modifiers.filter { it.expirationTimestamp > currentTimeMillis }

        // 2. Calculate Base Mood from Stats
        val baseMood = determineBaseMood(stats, world)

        // 3. Blend with active modifiers, temperament biases, and memories
        val weightedMood = blendMoods(baseMood, activeModifiers, psychology, memoryGraph, currentTimeMillis)

        // 4. Determine Intensity based on how extreme the stats are
        val newIntensity = calculateIntensity(stats, weightedMood)

        return currentEmotion.copy(
            primaryMood = weightedMood,
            intensity = newIntensity,
            modifiers = activeModifiers
        )
    }

    /**
     * Adds a new emotional modifier.
     */
    fun addModifier(
        current: EmotionState,
        mood: Mood,
        intensity: Float,
        durationMillis: Long,
        source: String
    ): EmotionState {
        val newModifier = MoodModifier(
            mood = mood,
            intensity = intensity,
            expirationTimestamp = System.currentTimeMillis() + durationMillis,
            source = source
        )
        
        // Remove existing modifiers from the same source to prevent stacking if desired
        val filtered = current.modifiers.filter { it.source != source }
        
        return current.copy(
            modifiers = (filtered + newModifier).takeLast(10) // Limit to 10 active modifiers
        )
    }

    /**
     * Processes significant events and potentially creates memories.
     */
    fun processMemory(
        psychology: PsychologyState,
        type: MemoryType,
        sourceId: String,
        valence: Float,
        intensity: Float
    ): PsychologyState {
        val newMemory = MemoryAnchor(
            type = type,
            sourceId = sourceId,
            emotionalValence = valence,
            intensity = intensity,
            timestamp = System.currentTimeMillis()
        )
        
        // Keep only relevant memories (e.g., last 20 significant events)
        val updatedMemories = (psychology.memories + newMemory)
            .sortedByDescending { it.timestamp }
            .take(20)

        return psychology.copy(memories = updatedMemories)
    }

    private fun determineBaseMood(stats: PetStats, world: WorldState): Mood {
        val base = when {
            stats.health < 20f -> Mood.SICK
            stats.energy < 15f -> Mood.SLEEPY
            stats.hunger < 15f -> Mood.ANGRY
            stats.happiness < 30f -> Mood.SAD
            stats.happiness > 85f -> Mood.EXCITED
            stats.happiness > 60f -> Mood.HAPPY
            stats.attention < 20f -> Mood.LONELY
            else -> Mood.RELAXED
        }
        
        // Weather Sensitivity
        return when (world.weather) {
            Weather.STORMY -> if (base == Mood.RELAXED || base == Mood.HAPPY) Mood.SAD else base
            Weather.RAINY -> if (base == Mood.RELAXED) Mood.SLEEPY else base
            else -> base
        }
    }

    private fun blendMoods(
        base: Mood,
        modifiers: List<MoodModifier>,
        psychology: PsychologyState,
        memoryGraph: EmotionMemoryGraph = EmotionMemoryGraph(),
        currentTime: Long = System.currentTimeMillis()
    ): Mood {
        val temperament = psychology.temperament
        
        // Influence of Memories (New Graph-based Logic)
        val traumaticMemories = memoryGraph.nodes.values.filter { it.valence < -0.7f && it.getCurrentWeight(currentTime) > 0.3f }

        if (traumaticMemories.isNotEmpty() && base == Mood.RELAXED) {
            // Traumatic memories make the buddy more prone to sadness or boredom
            return if (temperament.independence > 0.7f) Mood.BORED else Mood.SAD
        }

        if (modifiers.isEmpty()) {
            // Temperament biases
            if (temperament.laziness > 0.8f && base == Mood.RELAXED) return Mood.SLEEPY
            if (temperament.affection > 0.8f && base == Mood.RELAXED) return Mood.HAPPY
            return base
        }

        // Modifiers override base
        val strongestModifier = modifiers.maxByOrNull { it.intensity }
        return if (strongestModifier != null && (strongestModifier.intensity > 0.3f)) {
            strongestModifier.mood
        } else {
            base
        }
    }

    private fun calculateIntensity(stats: PetStats, mood: Mood): Float {
        return when (mood) {
            Mood.HAPPY -> (stats.happiness - 50f) / 50f
            Mood.ANGRY -> (100f - stats.hunger) / 100f
            Mood.SAD -> (100f - stats.happiness) / 100f
            Mood.SLEEPY -> (100f - stats.energy) / 100f
            Mood.LONELY -> (100f - stats.attention) / 100f
            else -> 0.5f
        }.coerceIn(0.1f, 1.0f)
    }
}
