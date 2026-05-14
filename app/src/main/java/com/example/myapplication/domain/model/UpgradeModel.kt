package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UpgradeCategory {
    ENERGY, HUNGER, HAPPINESS, ECONOMY, SLEEP
}

@Serializable
data class UpgradeModel(
    val id: String,
    val name: String,
    val description: String,
    val category: UpgradeCategory,
    val currentLevel: Int = 0,
    val maxLevel: Int = 10,
    val baseCost: Int = 100,
    val costMultiplier: Float = 1.5f
) {
    fun getNextLevelCost(): Int {
        return (baseCost * Math.pow(costMultiplier.toDouble(), currentLevel.toDouble())).toInt()
    }
    
    fun isMaxLevel(): Boolean = currentLevel >= maxLevel
}

object UpgradeRegistry {
    val allUpgrades = listOf(
        // --- ENERGY ---
        UpgradeModel(
            id = "energy_regen",
            name = "Stellar Recharge",
            description = "Increases energy regeneration while sleeping.",
            category = UpgradeCategory.ENERGY,
            baseCost = 150
        ),
        UpgradeModel(
            id = "energy_decay",
            name = "Endurance Training",
            description = "Reduces energy decay while awake.",
            category = UpgradeCategory.ENERGY,
            baseCost = 200
        ),
        
        // --- HUNGER ---
        UpgradeModel(
            id = "hunger_decay",
            name = "Slow Metabolism",
            description = "Reduces hunger decay rate.",
            category = UpgradeCategory.HUNGER,
            baseCost = 100
        ),
        UpgradeModel(
            id = "food_efficiency",
            name = "Gourmet Palate",
            description = "Increases hunger restored from food.",
            category = UpgradeCategory.HUNGER,
            baseCost = 250
        ),

        // --- HAPPINESS ---
        UpgradeModel(
            id = "happiness_decay",
            name = "Cheerfulness",
            description = "Reduces happiness decay rate.",
            category = UpgradeCategory.HAPPINESS,
            baseCost = 120
        ),
        UpgradeModel(
            id = "toy_efficiency",
            name = "Playful Spirit",
            description = "Increases happiness gain from toys.",
            category = UpgradeCategory.HAPPINESS,
            baseCost = 300
        ),

        // --- ECONOMY ---
        UpgradeModel(
            id = "minigame_reward",
            name = "Lucky Streak",
            description = "Increases coins earned from minigames.",
            category = UpgradeCategory.ECONOMY,
            baseCost = 500,
            costMultiplier = 2.0f
        ),
        UpgradeModel(
            id = "passive_income",
            name = "Investment Fund",
            description = "Earn coins passively over time.",
            category = UpgradeCategory.ECONOMY,
            baseCost = 1000,
            costMultiplier = 2.5f
        ),

        // --- SLEEP ---
        UpgradeModel(
            id = "sleep_quality",
            name = "Deep Sleep",
            description = "Reduces hunger decay while sleeping.",
            category = UpgradeCategory.SLEEP,
            baseCost = 200
        ),
        UpgradeModel(
            id = "fast_wake",
            name = "Quick Starter",
            description = "Increases happiness gain upon waking up.",
            category = UpgradeCategory.SLEEP,
            baseCost = 150
        )
    )
}
