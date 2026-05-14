package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {
    @Query("SELECT * FROM lifetime_statistics WHERE id = 'global_stats'")
    fun getStatistics(): Flow<StatisticsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatistics(statistics: StatisticsEntity)

    @Query("UPDATE lifetime_statistics SET totalFoodEaten = totalFoodEaten + :increment WHERE id = 'global_stats'")
    suspend fun incrementFoodEaten(increment: Int = 1)

    @Query("UPDATE lifetime_statistics SET interactionsCount = interactionsCount + :increment WHERE id = 'global_stats'")
    suspend fun incrementInteractions(increment: Int = 1)

    @Query("UPDATE lifetime_statistics SET miniGamesPlayed = miniGamesPlayed + :increment WHERE id = 'global_stats'")
    suspend fun incrementMiniGamesPlayed(increment: Int = 1)

    @Query("UPDATE lifetime_statistics SET totalCoinsEarned = totalCoinsEarned + :increment WHERE id = 'global_stats'")
    suspend fun incrementCoinsEarned(increment: Int)

    @Query("UPDATE lifetime_statistics SET totalCoinsSpent = totalCoinsSpent + :increment WHERE id = 'global_stats'")
    suspend fun incrementCoinsSpent(increment: Int)

    @Query("UPDATE lifetime_statistics SET sleepCyclesCompleted = sleepCyclesCompleted + :increment WHERE id = 'global_stats'")
    suspend fun incrementSleepCycles(increment: Int = 1)

    @Query("UPDATE lifetime_statistics SET totalPlayTimeMillis = totalPlayTimeMillis + :increment WHERE id = 'global_stats'")
    suspend fun incrementPlayTime(increment: Long)

    @Query("UPDATE lifetime_statistics SET maxHappinessReached = :happiness WHERE id = 'global_stats' AND maxHappinessReached < :happiness")
    suspend fun updateMaxHappiness(happiness: Float)

    @Query("UPDATE lifetime_statistics SET casinoWins = casinoWins + 1, biggestCasinoWin = CASE WHEN :payout > biggestCasinoWin THEN :payout ELSE biggestCasinoWin END WHERE id = 'global_stats'")
    suspend fun logCasinoWin(payout: Int)

    @Query("UPDATE lifetime_statistics SET casinoLosses = casinoLosses + 1 WHERE id = 'global_stats'")
    suspend fun logCasinoLoss()

    @Query("UPDATE lifetime_statistics SET casinoJackpots = casinoJackpots + 1 WHERE id = 'global_stats'")
    suspend fun logCasinoJackpot()

    @Query("UPDATE lifetime_statistics SET gamblingExposureCount = gamblingExposureCount + 1 WHERE id = 'global_stats'")
    suspend fun incrementGamblingExposure()

    @Query("UPDATE lifetime_statistics SET totalMarketProfit = CASE WHEN :profit > 0 THEN totalMarketProfit + :profit ELSE totalMarketProfit END, totalMarketLosses = CASE WHEN :profit < 0 THEN totalMarketLosses + ABS(:profit) ELSE totalMarketLosses END, bestTrade = CASE WHEN :profit > bestTrade THEN :profit ELSE bestTrade END, worstTrade = CASE WHEN :profit < worstTrade THEN :profit ELSE worstTrade END, totalTrades = totalTrades + 1 WHERE id = 'global_stats'")
    suspend fun logMarketTrade(profit: Int)

    @Query("UPDATE lifetime_statistics SET totalInvested = totalInvested + :invested, totalMarketEarned = totalMarketEarned + :earned WHERE id = 'global_stats'")
    suspend fun updateMarketStats(invested: Int, earned: Int)

    @Query("UPDATE lifetime_statistics SET prestigeMultiplier = :multiplier, rebirthCount = :rebirths WHERE id = 'global_stats'")
    suspend fun updatePrestige(multiplier: Float, rebirths: Int)
}
