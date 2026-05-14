package com.example.myapplication.data.repository

import com.example.myapplication.data.local.StatisticsDao
import com.example.myapplication.data.local.StatisticsEntity
import com.example.myapplication.domain.model.LifetimeStats
import com.example.myapplication.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepositoryImpl @Inject constructor(
    private val statisticsDao: StatisticsDao
) : StatisticsRepository {

    override fun getStatistics(): Flow<LifetimeStats> {
        return statisticsDao.getStatistics().map { entity ->
            entity?.let {
                LifetimeStats(
                    totalPlayTimeMillis = it.totalPlayTimeMillis,
                    totalFoodEaten = it.totalFoodEaten,
                    totalCoinsEarned = it.totalCoinsEarned,
                    totalCoinsSpent = it.totalCoinsSpent,
                    miniGamesPlayed = it.miniGamesPlayed,
                    interactionsCount = it.interactionsCount,
                    maxHappinessReached = it.maxHappinessReached,
                    daysSurvived = it.daysSurvived,
                    totalDistanceTraveled = it.totalDistanceTraveled,
                    sleepCyclesCompleted = it.sleepCyclesCompleted,
                    casinoWins = it.casinoWins,
                    casinoLosses = it.casinoLosses,
                    biggestCasinoWin = it.biggestCasinoWin,
                    casinoJackpots = it.casinoJackpots,
                    addictionIntensity = it.addictionIntensity,
                    gamblingExposureCount = it.gamblingExposureCount,
                    totalInvested = it.totalInvested,
                    totalMarketEarned = it.totalMarketEarned,
                    totalMarketProfit = it.totalMarketProfit,
                    totalMarketLosses = it.totalMarketLosses,
                    bestTrade = it.bestTrade,
                    worstTrade = it.worstTrade,
                    totalTrades = it.totalTrades,
                    prestigeMultiplier = it.prestigeMultiplier,
                    rebirthCount = it.rebirthCount
                )
            } ?: LifetimeStats()
        }
    }

    override suspend fun updatePrestige(multiplier: Float, rebirths: Int) {
        ensureStatisticsExists()
        statisticsDao.updatePrestige(multiplier, rebirths)
    }

    override suspend fun logCasinoWin(payout: Int) {
        ensureStatisticsExists()
        statisticsDao.logCasinoWin(payout)
    }

    override suspend fun logCasinoLoss() {
        ensureStatisticsExists()
        statisticsDao.logCasinoLoss()
    }

    override suspend fun logCasinoJackpot() {
        ensureStatisticsExists()
        statisticsDao.logCasinoJackpot()
    }

    override suspend fun incrementGamblingExposure() {
        ensureStatisticsExists()
        statisticsDao.incrementGamblingExposure()
    }

    override suspend fun logMarketTrade(profit: Int) {
        ensureStatisticsExists()
        statisticsDao.logMarketTrade(profit)
    }

    override suspend fun updateMarketStats(invested: Int, earned: Int) {
        ensureStatisticsExists()
        statisticsDao.updateMarketStats(invested, earned)
    }

    override suspend fun incrementFoodEaten(amount: Int) {
        ensureStatisticsExists()
        statisticsDao.incrementFoodEaten(amount)
    }

    override suspend fun incrementInteractions(amount: Int) {
        ensureStatisticsExists()
        statisticsDao.incrementInteractions(amount)
    }

    override suspend fun incrementMiniGamesPlayed(amount: Int) {
        ensureStatisticsExists()
        statisticsDao.incrementMiniGamesPlayed(amount)
    }

    override suspend fun incrementCoinsEarned(amount: Int) {
        ensureStatisticsExists()
        statisticsDao.incrementCoinsEarned(amount)
    }

    override suspend fun incrementCoinsSpent(amount: Int) {
        ensureStatisticsExists()
        statisticsDao.incrementCoinsSpent(amount)
    }

    override suspend fun incrementSleepCycles(amount: Int) {
        ensureStatisticsExists()
        statisticsDao.incrementSleepCycles(amount)
    }

    override suspend fun incrementPlayTime(millis: Long) {
        ensureStatisticsExists()
        statisticsDao.incrementPlayTime(millis)
    }

    override suspend fun updateMaxHappiness(happiness: Float) {
        ensureStatisticsExists()
        statisticsDao.updateMaxHappiness(happiness)
    }

    override suspend fun ensureStatisticsExists() {
        val stats = statisticsDao.getStatistics().take(1).firstOrNull()
        if (stats == null) {
            statisticsDao.insertStatistics(StatisticsEntity())
        }
    }
}
