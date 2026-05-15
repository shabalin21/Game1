package com.example.myapplication.core.modifier

import kotlinx.serialization.Serializable

/**
 * A wrapper for a stat that includes its own modifiers.
 */
@Serializable
data class ComputedStat(
    val baseValue: Float,
    val modifiers: List<Modifier> = emptyList()
) {
    val finalValue: Float
        get() = ModifierPipeline.calculate(baseValue, modifiers)
}
