package com.example.myapplication.domain.simulation.emotion

import com.example.myapplication.domain.model.*
import com.example.myapplication.core.modifier.Modifier
import com.example.myapplication.core.modifier.ModifierSource
import com.example.myapplication.core.modifier.ModifierType
import com.example.myapplication.core.modifier.ModifierRegistry
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

/**
 * CORE EMOTIONAL SIMULATION ENGINE.
 * Implements nonlinear emotional interactions and drift.
 */
@Singleton
class EmotionalEngine @Inject constructor() {

    /**
     * Ticks the emotional state based on elapsed hours.
     */
    fun tick(
        current: PsychologyState,
        stats: PetStats,
        world: WorldState,
        hours: Float
    ): PsychologyState {
        // 1. CALCULATE DRIFT
        var next = current

        // Stress increases if hunger is high (actually hunger stat low) or health low
        val physicalStress = (100f - stats.hunger) / 10f + (100f - stats.health) / 10f
        val weatherStress = if (world.weather == Weather.STORMY) 5f else 0f
        val stressChange = (physicalStress + weatherStress - 2f) * hours // Base relief of 2 units/hr
        next = next.copy(stress = (next.stress + stressChange).coerceIn(0f, 100f))

        // Burnout increases if stress is high for long
        val burnoutGrowth = if (next.stress > 70f) (next.stress - 70f) / 10f else -5f
        next = next.copy(burnout = (next.burnout + burnoutGrowth * hours).coerceIn(0f, 100f))

        // Loneliness increases if social stat is low
        val socialVoid = (100f - stats.social) / 5f
        next = next.copy(loneliness = (next.loneliness + (socialVoid - 5f) * hours).coerceIn(0f, 100f))

        // Motivation is killed by burnout and low confidence
        val motivationBase = (next.confidence / 2f) + (stats.happiness / 2f)
        val motivationPenalty = next.burnout * 0.8f
        next = next.copy(motivation = (motivationBase - motivationPenalty).coerceIn(0f, 100f))

        // Social Exhaustion increases during social activity (handeled by events)
        // Here we just apply slow decay
        next = next.copy(socialExhaustion = (next.socialExhaustion - 10f * hours).coerceIn(0f, 100f))

        // Discipline drifts towards stability
        val disciplineDrift = (current.temperament.resilience * 50f - next.discipline) * 0.01f
        next = next.copy(discipline = (next.discipline + disciplineDrift * hours).coerceIn(0f, 100f))

        return next
    }

    /**
     * Generates systemic modifiers based on emotional state.
     */
    fun resolveModifiers(psychology: PsychologyState): List<Modifier> {
        val modifiers = mutableListOf<Modifier>()

        // Burnout reduces work efficiency
        if (psychology.burnout > 50f) {
            modifiers.add(Modifier(
                id = "burnout_penalty",
                name = "Burnout",
                value = 1.0f - (psychology.burnout - 50f) / 100f,
                type = ModifierType.MULTIPLICATIVE,
                source = ModifierSource.EMOTIONAL,
                tag = ModifierRegistry.EFFICIENCY_WORK
            ))
        }

        // Stress increases energy decay
        if (psychology.stress > 40f) {
            modifiers.add(Modifier(
                id = "stress_decay",
                name = "High Stress",
                value = 1.0f + (psychology.stress / 50f),
                type = ModifierType.MULTIPLICATIVE,
                source = ModifierSource.EMOTIONAL,
                tag = ModifierRegistry.DECAY_ENERGY
            ))
        }

        // Confidence improves work efficiency
        modifiers.add(Modifier(
            id = "confidence_bonus",
            name = "Confidence",
            value = 1.0f + (psychology.confidence / 200f),
            type = ModifierType.MULTIPLICATIVE,
            source = ModifierSource.EMOTIONAL,
            tag = ModifierRegistry.EFFICIENCY_WORK
        ))

        return modifiers
    }
}
