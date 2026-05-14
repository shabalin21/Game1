package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for tracking lifetime statistics.
 * These are separated from the pet state to allow for atomic updates and persistence.
 */
@Entity(tableName = "lifetime_statistics")
data class StatisticsEntity(
    @PrimaryKey val id: String = "global_stats",
    val totalPlayTimeMillis: Long = 0,
    val totalFoodEaten: Int = 0,
    val totalCoinsEarned: Int = 0,
    val totalCoinsSpent: Int = 0,
    val miniGamesPlayed: Int = 0,
    val interactionsCount: Int = 0,
    val maxHappinessReached: Float = 0f,
    val daysSurvived: Int = 0,
    val totalDistanceTraveled: Float = 0f,
    val sleepCyclesCompleted: Int = 0,
    val casinoWins: Int = 0,
    val casinoLosses: Int = 0,
    val biggestCasinoWin: Int = 0,
    val casinoJackpots: Int = 0,
    val addictionIntensity: Float = 0f,
    val gamblingExposureCount: Int = 0,
    val totalInvested: Int = 0,
    val totalMarketEarned: Int = 0,
    val totalMarketProfit: Int = 0,
    val totalMarketLosses: Int = 0,
    val bestTrade: Int = 0,
    val worstTrade: Int = 0,
    val totalTrades: Int = 0,
    val prestigeMultiplier: Float = 1.0f,
    val rebirthCount: Int = 0
)
