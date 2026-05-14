package com.example.myapplication.domain.casino

import com.example.myapplication.domain.casino.engine.*
import com.example.myapplication.domain.casino.model.*
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import java.util.Locale

import com.example.myapplication.domain.repository.PetRepository

@Singleton
class CasinoManager @Inject constructor(
    private val economyRepository: EconomyRepository,
    private val petRepository: PetRepository,
    private val logManager: TerminalLogManager,
    private val blackjackEngine: BlackjackEngine,
    private val slotEngine: SlotMachineEngine,
    private val coinFlipEngine: CoinFlipEngine,
    private val crashEngine: CrashGameEngine,
    private val plinkoEngine: PlinkoEngine,
    private val stateManager: CasinoStateManager,
    private val betValidation: BetValidationSystem,
    private val rewardDistribution: RewardDistributionEngine,
    private val cheatManager: com.example.myapplication.domain.admin.CheatManager
) {
    private val ENTRY_COST = 100
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var crashJob: Job? = null

    suspend fun tryEnterCasino(): Boolean {
        if (cheatManager.bypassCasinoFee.value) {
            logManager.log(LogCategory.CASINO, "ADMIN_BYPASS_ACTIVE: Entry granted.")
            return true
        }

        val pet = petRepository.getPetState().first() ?: return false
        
        // Check for Ban
        if (pet.casinoSession.isBanned) {
            if (System.currentTimeMillis() > pet.casinoSession.banExpiryTimestamp) {
                // Lift Ban
                petRepository.savePetState(pet.copy(
                    casinoSession = pet.casinoSession.copy(isBanned = false, sessionWins = 0)
                ))
            } else {
                logManager.log(LogCategory.CRITICAL, "ACCESS_DENIED: Security has flagged your profile.")
                return false
            }
        }

        val coins = economyRepository.getCoins().first()
        val entryFee = if (pet.casinoSession.sessionWins >= 5) pet.casinoSession.reEntryFee else ENTRY_COST
        
        if (coins < entryFee) {
            logManager.log(LogCategory.ECONOMY, "Casino access denied. Insufficient balance ($entryFee CR required).")
            return false
        }
        
        economyRepository.spendCoins(entryFee)
        logManager.log(LogCategory.CASINO, "Neural gaming authorization confirmed. Fee paid: $entryFee CR")
        return true
    }

    // --- BLACKJACK ---

    suspend fun startBlackjack(bet: Int) {
        val validation = betValidation.validateBet(bet)
        if (validation !is BetValidationResult.Valid) {
            logManager.log(LogCategory.WARNING, "Bet rejected: $validation")
            return
        }

        economyRepository.spendCoins(bet)
        blackjackEngine.reshuffle()
        
        val playerHand = listOf(blackjackEngine.drawCard(), blackjackEngine.drawCard())
        val dealerHand = listOf(blackjackEngine.drawCard(), blackjackEngine.drawCard())
        
        stateManager.updateBlackjackState(BlackjackState.PlayerTurn(playerHand, dealerHand, bet))
        
        if (blackjackEngine.isBlackjack(playerHand)) {
            resolveBlackjack()
        }
    }

    suspend fun blackjackHit() {
        val currentState = stateManager.blackjackState.value
        if (currentState is BlackjackState.PlayerTurn) {
            val newHand = currentState.playerHand + blackjackEngine.drawCard()
            if (blackjackEngine.isBust(newHand)) {
                stateManager.updateBlackjackState(BlackjackState.Result(newHand, currentState.dealerHand, BlackjackOutcome.BUST, 0, currentState.bet))
                rewardDistribution.processLoss(0, "BLACKJACK")
            } else {
                stateManager.updateBlackjackState(BlackjackState.PlayerTurn(newHand, currentState.dealerHand, currentState.bet))
            }
        }
    }

    suspend fun blackjackStand() {
        val currentState = stateManager.blackjackState.value
        if (currentState is BlackjackState.PlayerTurn) {
            var dealerHand = currentState.dealerHand
            while (blackjackEngine.shouldDealerHit(dealerHand)) {
                dealerHand = dealerHand + blackjackEngine.drawCard()
            }
            
            val outcome = blackjackEngine.determineOutcome(currentState.playerHand, dealerHand)
            val payout = blackjackEngine.calculatePayout(currentState.bet, outcome) 
            stateManager.updateBlackjackState(BlackjackState.Result(currentState.playerHand, dealerHand, outcome, payout, currentState.bet))
            
            if (payout > 0) rewardDistribution.distributeWin(payout, "BLACKJACK")
            else rewardDistribution.processLoss(currentState.bet, "BLACKJACK")
        }
    }

    private suspend fun resolveBlackjack() {
        val currentState = stateManager.blackjackState.value
        if (currentState is BlackjackState.PlayerTurn) {
            val outcome = blackjackEngine.determineOutcome(currentState.playerHand, currentState.dealerHand)
            val payout = blackjackEngine.calculatePayout(currentState.bet, outcome)
            stateManager.updateBlackjackState(BlackjackState.Result(currentState.playerHand, currentState.dealerHand, outcome, payout, currentState.bet))
            if (payout > 0) rewardDistribution.distributeWin(payout, "BLACKJACK")
        }
    }

    // --- SLOTS ---

    suspend fun playSlots(bet: Int) {
        val validation = betValidation.validateBet(bet)
        if (validation !is BetValidationResult.Valid) return

        economyRepository.spendCoins(bet)
        val result = slotEngine.spin(bet)
        stateManager.updateSlotsState(result)
        
        if (result.payout > 0) {
            rewardDistribution.distributeWin(result.payout, "SLOTS", result.isJackpot)
        } else {
            rewardDistribution.processLoss(bet, "SLOTS")
        }
    }

    // --- COIN FLIP ---

    suspend fun playCoinFlip(bet: Int, side: CoinSide) {
        val validation = betValidation.validateBet(bet)
        if (validation !is BetValidationResult.Valid) return

        economyRepository.spendCoins(bet)
        val result = coinFlipEngine.flip(bet, side)
        stateManager.updateCoinFlipState(result)

        if (result.isWin) {
            rewardDistribution.distributeWin(result.payout, "COINFLIP")
        } else {
            rewardDistribution.processLoss(bet, "COINFLIP")
        }
    }

    // --- CRASH ---

    fun startCrash(bet: Int) {
        if (crashJob?.isActive == true) return
        
        crashJob = scope.launch {
            val validation = betValidation.validateBet(bet)
            if (validation !is BetValidationResult.Valid) return@launch
            
            economyRepository.spendCoins(bet)
            val crashPoint = crashEngine.generateCrashPoint()
            var currentMultiplier = 1.00f
            
            logManager.log(LogCategory.CASINO, "Crash session initialized. Bet: $bet")
            
            while (currentMultiplier < crashPoint) {
                stateManager.updateCrashState(CrashState.Rising(currentMultiplier, bet))
                delay(100)
                currentMultiplier += 0.01f * (currentMultiplier * 0.5f).coerceAtLeast(1f)
                
                if (currentMultiplier > 2.0f && Random.nextDouble() < 0.01) {
                    logManager.log(LogCategory.WARNING, "Neural interference detected.")
                }
            }
            
            stateManager.updateCrashState(CrashState.Crashed(crashPoint, bet))
            rewardDistribution.processLoss(bet, "CRASH")
            logManager.log(LogCategory.CRITICAL, "Market crash at ${String.format(Locale.US, "%.2f", crashPoint)}x")
        }
    }

    fun cashOutCrash() {
        val state = stateManager.crashState.value
        if (state is CrashState.Rising) {
            crashJob?.cancel()
            val payout = crashEngine.calculateCurrentPayout(state.betAmount, state.multiplier)
            stateManager.updateCrashState(CrashState.CashedOut(state.multiplier, payout))
            
            scope.launch {
                rewardDistribution.distributeWin(payout, "CRASH")
            }
            logManager.log(LogCategory.CASINO, "Emergency cash-out executed at ${String.format(Locale.US, "%.2f", state.multiplier)}x")
        }
    }

    // --- PLINKO ---

    suspend fun dropPlinkoBall(bet: Int) {
        val validation = betValidation.validateBet(bet)
        if (validation !is BetValidationResult.Valid) return

        economyRepository.spendCoins(bet)
        
        scope.launch {
            val ballId = System.currentTimeMillis()
            val risk = stateManager.plinkoState.value.activeRisk
            var ball = PlinkoBall(
                id = ballId,
                x = (Random.nextFloat() - 0.5f) * 0.2f,
                y = 0f
            )

            stateManager.updatePlinkoState(
                stateManager.plinkoState.value.copy(
                    balls = stateManager.plinkoState.value.balls + ball
                )
            )

            while (!ball.isFinished) {
                ball = plinkoEngine.updateBall(ball)
                updateBallInState(ball)
                delay(16)
            }

            val multipliers = plinkoEngine.getMultipliers(risk)
            val index = plinkoEngine.calculateResultIndex(ball.x)
            val multiplier = multipliers.getOrElse(index) { 1.0f }
            val payout = (bet * multiplier).toInt()

            if (payout > 0) {
                rewardDistribution.distributeWin(payout, "PLINKO")
                logManager.log(LogCategory.CASINO, "Neural path confirmed: ${payout}CR (${multiplier}x)")
            } else {
                rewardDistribution.processLoss(bet, "PLINKO")
            }
            
            stateManager.updatePlinkoState(
                stateManager.plinkoState.value.copy(
                    recentPayouts = (listOf(multiplier) + stateManager.plinkoState.value.recentPayouts).take(10)
                )
            )

            delay(1500)
            removeBallFromState(ballId)
        }
    }

    private fun updateBallInState(ball: PlinkoBall) {
        val currentState = stateManager.plinkoState.value
        val updatedBalls = currentState.balls.map { if (it.id == ball.id) ball else it }
        stateManager.updatePlinkoState(currentState.copy(balls = updatedBalls))
    }

    private fun removeBallFromState(ballId: Long) {
        val currentState = stateManager.plinkoState.value
        val filteredBalls = currentState.balls.filter { it.id != ballId }
        stateManager.updatePlinkoState(currentState.copy(balls = filteredBalls))
    }
}
