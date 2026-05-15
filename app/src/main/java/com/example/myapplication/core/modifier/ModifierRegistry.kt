package com.example.myapplication.core.modifier

/**
 * Registry of standard modifier tags and constants.
 */
object ModifierRegistry {
    // Stat Keys
    const val STAT_HUNGER = "hunger"
    const val STAT_ENERGY = "energy"
    const val STAT_HAPPINESS = "happiness"
    const val STAT_HEALTH = "health"
    const val STAT_STRESS = "stress"
    const val STAT_SOCIAL = "social"
    const val STAT_INTELLIGENCE = "intelligence"
    const val STAT_FITNESS = "fitness"
    const val STAT_COMFORT = "comfort"
    const val STAT_HYGIENE = "hygiene"

    // Economy Keys
    const val ECONOMY_INCOME = "income"
    const val ECONOMY_EXPENSE = "expense"
    const val ECONOMY_MARKET_VOLATILITY = "market_volatility"

    // Efficiency Keys
    const val EFFICIENCY_WORK = "work_efficiency"
    const val EFFICIENCY_GYM = "gym_efficiency"
    const val EFFICIENCY_STUDY = "study_efficiency"
    
    // Decay Keys
    const val DECAY_ENERGY = "energy_decay"
    const val DECAY_HUNGER = "hunger_decay"
    const val DECAY_HAPPINESS = "happiness_decay"
    
    // Regen Keys
    const val REGEN_ENERGY = "energy_regen"
    const val REGEN_HEALTH = "health_regen"
}
