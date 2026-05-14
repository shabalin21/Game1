package com.example.myapplication.domain.simulation

/**
 * CENTRALIZED SIMULATION CONSTANTS.
 * Use this file to tune the gameplay loop balance.
 */
object SimulationConstants {
    
    // --- 1. CORE NEEDS DECAY (Per Hour) ---
    // These values determine how fast the stats drop when the pet is awake.
    const val AWAKE_HUNGER_DECAY = 10.0f    // ~10h to empty
    const val AWAKE_ENERGY_DECAY = 12.0f    // ~8.3h to empty
    const val AWAKE_HAPPINESS_DECAY = 8.0f  // ~12.5h to empty (base rate)
    
    // --- 2. SLEEP RATES (Per Hour) ---
    const val SLEEP_ENERGY_RECOVERY = 25.0f // ~4h to full
    const val SLEEP_HUNGER_DECAY = 4.0f     // Slower decay during sleep
    const val SLEEP_HAPPINESS_DECAY = 2.0f  // Very slow decay during sleep
    
    // --- 3. THRESHOLDS & PENALTIES ---
    const val CRITICAL_NEED_THRESHOLD = 20.0f
    const val HAPPINESS_PENALTY_MULTIPLIER = 1.5f // How much low needs impact happiness
    
    // --- 4. MAX VALUES ---
    const val MAX_STAT_VALUE = 100.0f
    const val MIN_STAT_VALUE = 0.0f
    
    // --- 5. TIME JUMP LIMITS ---
    const val MAX_SIMULATION_JUMP_HOURS = 24L
}
