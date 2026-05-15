package com.example.myapplication.domain.simulation.economy

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class EconomySimulation @Inject constructor() {
    
    private var globalInflationRate = 1.0f
    private var marketSaturation = 0.5f

    fun tick() {
        // Slow inflation drift
        globalInflationRate += (Random.nextFloat() - 0.45f) * 0.001f // Bias slightly positive
        globalInflationRate = globalInflationRate.coerceIn(0.5f, 5.0f)
        
        // Saturation drift
        marketSaturation += (Random.nextFloat() - 0.5f) * 0.01f
        marketSaturation = marketSaturation.coerceIn(0f, 1f)
    }

    fun getPriceMultiplier(category: String): Float {
        val categoryBias = when(category) {
            "FOOD" -> 1.1f
            "LUXURY" -> 1.5f * marketSaturation
            else -> 1.0f
        }
        return globalInflationRate * categoryBias
    }
}
