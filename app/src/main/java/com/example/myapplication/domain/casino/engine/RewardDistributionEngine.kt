package com.example.myapplication.domain.casino.engine

import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.event.GameplayEventManager
import com.example.myapplication.domain.model.Mood
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.simulation.MoodEngine
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardDistributionEngine @Inject constructor(
    private val economyRepository: EconomyRepository,
    private val petRepository: PetRepository,
    private val moodEngine: MoodEngine,
    private val logManager: TerminalLogManager,
    private val eventManager: GameplayEventManager
) {

    suspend fun distributeWin(amount: Int, game: String, isJackpot: Boolean = false) {
        economyRepository.addCoins(amount)
        logManager.log(LogCategory.CASINO, "[${game.uppercase()}] SUCCESS: +$amount CR")
        
        eventManager.dispatch(GameplayEvent.CasinoWin(game, amount, isJackpot))
        eventManager.dispatch(GameplayEvent.CasinoGamePlayed(game, 0))

        applyPsychologicalWin(amount)
    }

    suspend fun processLoss(amount: Int, game: String) {
        logManager.log(LogCategory.CASINO, "[${game.uppercase()}] DEFICIT: -$amount CR")
        
        eventManager.dispatch(GameplayEvent.CasinoLoss(game, amount))
        eventManager.dispatch(GameplayEvent.CasinoGamePlayed(game, amount))

        applyPsychologicalLoss(amount)
    }

    private suspend fun applyPsychologicalWin(amount: Int) {
        val pet = petRepository.getPetState().first() ?: return
        
        // Casino Security Tracking
        val newSessionWins = pet.casinoSession.sessionWins + 1
        val shouldBan = newSessionWins >= 10
        
        // Dopamine Spike
        val dopamineGain = (amount / 10f).coerceIn(5f, 40f)
        val happinessGain = (amount / 20f).coerceIn(2f, 15f)
        
        val newEmotion = moodEngine.addModifier(
            pet.emotionState,
            Mood.EXCITED,
            0.6f,
            60000 * 10,
            "CASINO_WIN_ADRENALINE"
        )

        petRepository.savePetState(pet.copy(
            emotionState = newEmotion,
            stats = pet.stats.copy(
                happiness = (pet.stats.happiness + happinessGain).coerceAtMost(100f),
                stress = (pet.stats.stress - 5f).coerceAtLeast(0f)
            ),
            psychology = pet.psychology.copy(
                dopamineLevel = (pet.psychology.dopamineLevel + dopamineGain).coerceAtMost(100f),
                addictionLevel = (pet.psychology.addictionLevel + 0.5f).coerceAtMost(100f),
                impulseControl = (pet.psychology.impulseControl - 0.2f).coerceAtLeast(0f),
                emotionalStability = (pet.psychology.emotionalStability + 1f).coerceAtMost(100f)
            ),
            casinoSession = pet.casinoSession.copy(
                sessionWins = newSessionWins,
                isBanned = shouldBan,
                banExpiryTimestamp = if (shouldBan) System.currentTimeMillis() + (1000 * 60 * 60) else 0 // 1 hour ban
            )
        ))
        
        if (shouldBan) {
            logManager.log(LogCategory.WARNING, "SECURITY: Winning streak outlier. Access suspended.")
        }
    }

    private suspend fun applyPsychologicalLoss(amount: Int) {
        val pet = petRepository.getPetState().first() ?: return
        
        // Stress and frustration
        val stressGain = (amount / 20f).coerceIn(5f, 20f)
        
        petRepository.savePetState(pet.copy(
            stats = pet.stats.copy(
                happiness = (pet.stats.happiness - 3f).coerceAtLeast(0f),
                stress = (pet.stats.stress + stressGain).coerceAtMost(100f)
            ),
            psychology = pet.psychology.copy(
                dopamineLevel = (pet.psychology.dopamineLevel - 10f).coerceAtLeast(0f),
                addictionLevel = (pet.psychology.addictionLevel + 0.3f).coerceAtMost(100f),
                emotionalStability = (pet.psychology.emotionalStability - 2f).coerceAtLeast(0f),
                impulseControl = (pet.psychology.impulseControl - 0.5f).coerceAtLeast(0f)
            )
        ))
    }
}
