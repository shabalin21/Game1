package com.example.myapplication.ui.screen.casino

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.casino.*
import com.example.myapplication.domain.casino.model.*
import com.example.myapplication.domain.admin.AdminCommandProcessor
import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.terminal.TerminalLogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CasinoViewModel @Inject constructor(
    private val intentDispatcher: com.example.myapplication.domain.event.IntentDispatcher,
    private val petRepository: com.example.myapplication.domain.repository.PetRepository,
    private val economyRepository: EconomyRepository,
    private val stateManager: CasinoStateManager,
    private val performanceMonitor: com.example.myapplication.util.PerformanceMonitor,
    val logManager: TerminalLogManager
) : ViewModel() {

    val coins = economyRepository.getCoins()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val petState = petRepository.getPetState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val fps = performanceMonitor.fps
    val blackjackState = stateManager.blackjackState
    val slotsState = stateManager.slotsState
    val coinFlipState = stateManager.coinFlipState
    val crashState = stateManager.crashState
    val plinkoState = stateManager.plinkoState

    private val _entryAuthorized = MutableStateFlow(false)
    val entryAuthorized: StateFlow<Boolean> = _entryAuthorized.asStateFlow()

    fun tryEnter() {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.TryEnter)
    }

    fun onAdminCommand(command: String) {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Admin.ExecuteCommand(command))
    }

    fun startBlackjack(bet: Int) {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.StartBlackjack(bet))
    }

    fun blackjackHit() {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.BlackjackHit)
    }

    fun blackjackStand() {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.BlackjackStand)
    }

    fun playSlots(bet: Int) {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.PlaySlots(bet))
    }

    fun playCoinFlip(bet: Int, side: CoinSide) {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.PlayCoinFlip(bet, side))
    }

    fun startCrash(bet: Int) {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.StartCrash(bet))
    }

    fun cashOutCrash() {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.CashOutCrash)
    }

    fun dropPlinkoBall(bet: Int) {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.DropPlinkoBall(bet))
    }

    fun setPlinkoRisk(risk: PlinkoRisk) {
        stateManager.setPlinkoRisk(risk)
    }

    fun payReEntry() {
        intentDispatcher.dispatch(com.example.myapplication.domain.event.GameplayIntent.Casino.PayReEntry)
    }
}
