package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LifetimeStats(
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
    // Casino Stats
    val casinoWins: Int = 0,
    val casinoLosses: Int = 0,
    val biggestCasinoWin: Int = 0,
    val casinoJackpots: Int = 0,
    // Addiction / Psychological
    val addictionIntensity: Float = 0f,
    val gamblingExposureCount: Int = 0,
    // Market Stats
    val totalInvested: Int = 0,
    val totalMarketEarned: Int = 0,
    val totalMarketProfit: Int = 0,
    val totalMarketLosses: Int = 0,
    val bestTrade: Int = 0,
    val worstTrade: Int = 0,
    val totalTrades: Int = 0,
    // Prestige System
    val prestigeMultiplier: Float = 1.0f,
    val rebirthCount: Int = 0
)
