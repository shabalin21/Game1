package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persists developer cheat states across sessions.
 */
@Entity(tableName = "debug_cheats")
data class DebugCheatEntity(
    @PrimaryKey val id: String = "global_cheats",
    val isActivated: Boolean = false,
    val godModeEnabled: Boolean = false,
    val frozenStatsJson: String = "{}", // Map<String, Float> serialized
    val simulationPaused: Boolean = false,
    val timeDilation: Float = 1.0f,
    val particlesEnabled: Boolean = true,
    val glowEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val blurEnabled: Boolean = true,
    val showFpsCounter: Boolean = false,
    val showPerformanceOverlay: Boolean = false,
    val showMemoryUsage: Boolean = false,
    val infiniteCoins: Boolean = false
)
