package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.LifetimeStats
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun getStatistics(): Flow<LifetimeStats>
    suspend fun incrementFoodEaten(amount: Int = 1)
    suspend fun incrementInteractions(amount: Int = 1)
    suspend fun incrementMiniGamesPlayed(amount: Int = 1)
    suspend fun incrementCoinsEarned(amount: Int)
    suspend fun incrementCoinsSpent(amount: Int)
    suspend fun incrementSleepCycles(amount: Int = 1)
    suspend fun incrementPlayTime(millis: Long)
    suspend fun updateMaxHappiness(happiness: Float)
    suspend fun logCasinoWin(payout: Int)
    suspend fun logCasinoLoss()
    suspend fun logCasinoJackpot()
    suspend fun incrementGamblingExposure()
    suspend fun logMarketTrade(profit: Int)
    suspend fun updateMarketStats(invested: Int, earned: Int)
    suspend fun updatePrestige(multiplier: Float, rebirths: Int)
    suspend fun ensureStatisticsExists()
}
