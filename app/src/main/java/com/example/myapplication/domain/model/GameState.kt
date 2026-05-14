package com.example.myapplication.domain.model

import com.example.myapplication.domain.repository.InventoryItem

/**
 * Unified state object representing the entire game world.
 */
data class GameState(
    val pet: PetModel? = null,
    val inventory: List<InventoryItem> = emptyList(),
    val coins: Int = 0,
    val statistics: LifetimeStats = LifetimeStats(),
    val settings: SettingsModel = SettingsModel(),
    val upgrades: List<UpgradeModel> = emptyList(),
    val world: WorldState = WorldState()
)
