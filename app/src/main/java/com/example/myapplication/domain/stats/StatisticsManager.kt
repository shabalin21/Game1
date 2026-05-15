package com.example.myapplication.domain.stats

import com.example.myapplication.core.EventBus
import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.event.TransactionType
import com.example.myapplication.domain.repository.StatisticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsManager @Inject constructor(
    private val eventBus: EventBus,
    private val statisticsRepository: StatisticsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        eventBus.events
            .onEach { event ->
                handleEvent(event)
            }
            .launchIn(scope)
    }

    private suspend fun handleEvent(event: GameplayEvent) {
        when (event) {
            is GameplayEvent.FoodConsumed -> {
                statisticsRepository.incrementFoodEaten()
            }
            is GameplayEvent.PetInteracted -> {
                statisticsRepository.incrementInteractions()
            }
            is GameplayEvent.MinigameCompleted -> {
                statisticsRepository.incrementMiniGamesPlayed()
                // Coins are handled by EconomyTransaction
            }
            is GameplayEvent.EconomyTransaction -> {
                if (event.type == TransactionType.EARNED) {
                    statisticsRepository.incrementCoinsEarned(event.amount)
                } else {
                    statisticsRepository.incrementCoinsSpent(event.amount)
                }
            }
            is GameplayEvent.PetSlept -> {
                statisticsRepository.incrementSleepCycles()
            }
            is GameplayEvent.CasinoWin -> {
                statisticsRepository.logCasinoWin(event.payout)
                if (event.isJackpot) statisticsRepository.logCasinoJackpot()
            }
            is GameplayEvent.CasinoLoss -> {
                statisticsRepository.logCasinoLoss()
            }
            is GameplayEvent.CasinoGamePlayed -> {
                statisticsRepository.incrementGamblingExposure()
            }
            is GameplayEvent.LevelUp -> {
                // Future: handle level up stats
            }
            else -> { /* Ignore other events */ }
        }
    }
}
