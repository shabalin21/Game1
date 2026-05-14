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
    private val casinoManager: CasinoManager,
    private val petRepository: com.example.myapplication.domain.repository.PetRepository,
    private val economyRepository: EconomyRepository,
    private val stateManager: CasinoStateManager,
    private val adminProcessor: AdminCommandProcessor,
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
        viewModelScope.launch {
            if (casinoManager.tryEnterCasino()) {
                _entryAuthorized.value = true
            }
        }
    }

    fun onAdminCommand(command: String) {
        adminProcessor.processCommand(command)
    }

    fun startBlackjack(bet: Int) {
        viewModelScope.launch { casinoManager.startBlackjack(bet) }
    }

    fun blackjackHit() {
        viewModelScope.launch { casinoManager.blackjackHit() }
    }

    fun blackjackStand() {
        viewModelScope.launch { casinoManager.blackjackStand() }
    }

    fun playSlots(bet: Int) {
        viewModelScope.launch { casinoManager.playSlots(bet) }
    }

    fun playCoinFlip(bet: Int, side: CoinSide) {
        viewModelScope.launch { casinoManager.playCoinFlip(bet, side) }
    }

    fun startCrash(bet: Int) {
        casinoManager.startCrash(bet)
    }

    fun cashOutCrash() {
        casinoManager.cashOutCrash()
    }

    fun dropPlinkoBall(bet: Int) {
        viewModelScope.launch { casinoManager.dropPlinkoBall(bet) }
    }

    fun setPlinkoRisk(risk: PlinkoRisk) {
        stateManager.setPlinkoRisk(risk)
    }

    fun payReEntry() {
        viewModelScope.launch {
            val pet = petState.value ?: return@launch
            if (economyRepository.spendCoins(pet.casinoSession.reEntryFee)) {
                petRepository.savePetState(pet.copy(
                    casinoSession = pet.casinoSession.copy(isBanned = false, sessionWins = 0)
                ))
            }
        }
    }
}
