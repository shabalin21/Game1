package com.example.myapplication.core.modifier

import timber.log.Timber

/**
 * Centrally calculates final values based on layered modifiers.
 * Formula: FinalValue = ((Base + AdditiveSum) * MultiplierProduct)
 */
object ModifierPipeline {

    fun calculate(baseValue: Float, modifiers: List<Modifier>): Float {
        // 1. Filter expired
        val activeModifiers = modifiers.filter { !it.isExpired }
        
        // 2. Sum Additive
        val additiveSum = activeModifiers
            .filter { it.type == ModifierType.ADDITIVE }
            .sumOf { it.value.toDouble() }
            .toFloat()

        // 3. Product of Multipliers
        var multiplierProduct = 1.0f
        activeModifiers
            .filter { it.type == ModifierType.MULTIPLICATIVE }
            .forEach { multiplierProduct *= it.value }

        val finalValue = (baseValue + additiveSum) * multiplierProduct
        
        if (modifiers.size != activeModifiers.size) {
            Timber.d("ModifierPipeline: Cleaned up ${modifiers.size - activeModifiers.size} expired modifiers.")
        }

        return finalValue
    }
}
