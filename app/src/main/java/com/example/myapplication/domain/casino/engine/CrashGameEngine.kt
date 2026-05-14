package com.example.myapplication.domain.casino.engine

import com.example.myapplication.domain.casino.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class CrashGameEngine @Inject constructor() {

    /**
     * Generates a crash point based on weighted probability.
     * Higher multipliers are rarer.
     */
    fun generateCrashPoint(): Float {
        val rand = Random.nextDouble()
        
        // 3% chance of instant crash at 1.00x
        if (rand < 0.03) return 1.00f
        
        // Formula: 1 / (1 - X) where X is uniform random
        // We adjust it to favor 1.2x - 3x range
        val crash = (1.0 / (1.0 - Random.nextDouble())).toFloat()
        
        // Cap it or adjust for ultra-rare high runs
        return if (crash > 50f) {
            // Ultra-rare 50x+ (reduced further)
            if (Random.nextDouble() < 0.2) crash else 49.9f
        } else {
            crash
        }
    }

    fun calculateCurrentPayout(betAmount: Int, currentMultiplier: Float): Int {
        return (betAmount * currentMultiplier).toInt()
    }
}
