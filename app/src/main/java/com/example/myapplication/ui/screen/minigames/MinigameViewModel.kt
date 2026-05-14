package com.example.myapplication.ui.screen.minigames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.event.GameplayEventManager
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.repository.UpgradeRepository
import com.example.myapplication.domain.simulation.SimulationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MinigameViewModel @Inject constructor(
    private val economyRepository: EconomyRepository,
    private val petRepository: PetRepository,
    private val upgradeRepository: UpgradeRepository,
    private val simulationManager: SimulationManager,
    private val eventManager: GameplayEventManager
) : ViewModel() {

    fun rewardTapRush(score: Int) {
        viewModelScope.launch {
            val multiplier = getRewardMultiplier()
            val coins = (score / 10 * multiplier).toInt() // REBALANCED: From score/5
            if (coins > 0) {
                economyRepository.addCoins(coins)
            }
            simulationManager.processMinigameResult(score)
            eventManager.dispatch(GameplayEvent.MinigameCompleted("tap_rush", score, coins))
        }
    }

    fun rewardMemoryMatch(score: Int) {
        viewModelScope.launch {
            val multiplier = getRewardMultiplier()
            val coins = (50 * multiplier).toInt() // REBALANCED: From 100
            economyRepository.addCoins(coins)
            simulationManager.processMinigameResult(score)
            eventManager.dispatch(GameplayEvent.MinigameCompleted("memory_match", score, coins))
        }
    }

    fun rewardReactionTap(score: Int) {
        viewModelScope.launch {
            val multiplier = getRewardMultiplier()
            val coins = (score / 8 * multiplier).toInt() // REBALANCED: From score/3
            if (coins > 0) {
                economyRepository.addCoins(coins)
            }
            simulationManager.processMinigameResult(score)
            eventManager.dispatch(GameplayEvent.MinigameCompleted("reaction_tap", score, coins))
        }
    }

    fun rewardNeonDodge(score: Int) {
        viewModelScope.launch {
            val multiplier = getRewardMultiplier()
            val coins = (score / 25 * multiplier).toInt() // REBALANCED: From score/10
            if (coins > 0) {
                economyRepository.addCoins(coins)
            }
            simulationManager.processMinigameResult(score)
            eventManager.dispatch(GameplayEvent.MinigameCompleted("neon_dodge", score, coins))
        }
    }

    fun rewardNeonHack(score: Int) {
        viewModelScope.launch {
            val multiplier = getRewardMultiplier()
            val coins = (score / 5 * multiplier).toInt() // REBALANCED: From score/2
            if (coins > 0) {
                economyRepository.addCoins(coins)
            }
            simulationManager.processMinigameResult(score)
            eventManager.dispatch(GameplayEvent.MinigameCompleted("neon_hack", score, coins))
        }
    }

    fun rewardVoidRunner(score: Int) {
        viewModelScope.launch {
            val multiplier = getRewardMultiplier()
            val coins = (score / 50 * multiplier).toInt() // REBALANCED: From score/20
            if (coins > 0) {
                economyRepository.addCoins(coins)
            }
            simulationManager.processMinigameResult(score)
            eventManager.dispatch(GameplayEvent.MinigameCompleted("void_runner", score, coins))
        }
    }

    private suspend fun getRewardMultiplier(): Float {
        val upgrades = upgradeRepository.getUpgrades().first()
        val level = upgrades.find { it.id == "minigame_reward" }?.currentLevel ?: 0
        return 1.0f + (level * 0.2f)
    }
}
