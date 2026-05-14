package com.example.myapplication.domain.casino

import com.example.myapplication.domain.casino.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CasinoStateManager @Inject constructor() {

    private val _blackjackState = MutableStateFlow<BlackjackState>(BlackjackState.Idle)
    val blackjackState: StateFlow<BlackjackState> = _blackjackState.asStateFlow()

    private val _slotsState = MutableStateFlow<SlotResult?>(null)
    val slotsState: StateFlow<SlotResult?> = _slotsState.asStateFlow()

    private val _coinFlipState = MutableStateFlow<CoinFlipResult?>(null)
    val coinFlipState: StateFlow<CoinFlipResult?> = _coinFlipState.asStateFlow()

    private val _crashState = MutableStateFlow<CrashState>(CrashState.Idle)
    val crashState: StateFlow<CrashState> = _crashState.asStateFlow()

    private val _plinkoState = MutableStateFlow(PlinkoState())
    val plinkoState: StateFlow<PlinkoState> = _plinkoState.asStateFlow()

    fun updateBlackjackState(state: BlackjackState) {
        _blackjackState.value = state
    }

    fun updateSlotsState(state: SlotResult?) {
        _slotsState.value = state
    }

    fun updateCoinFlipState(state: CoinFlipResult?) {
        _coinFlipState.value = state
    }

    fun updateCrashState(state: CrashState) {
        _crashState.value = state
    }

    fun updatePlinkoState(state: PlinkoState) {
        _plinkoState.value = state
    }

    fun setPlinkoRisk(risk: PlinkoRisk) {
        _plinkoState.value = _plinkoState.value.copy(activeRisk = risk)
    }
}
