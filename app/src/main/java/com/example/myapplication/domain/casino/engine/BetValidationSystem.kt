package com.example.myapplication.domain.casino.engine

import com.example.myapplication.domain.repository.EconomyRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BetValidationSystem @Inject constructor(
    private val economyRepository: EconomyRepository
) {
    private val MIN_BET = 10
    private val MAX_BET = 10000

    suspend fun validateBet(amount: Int): BetValidationResult {
        if (amount < MIN_BET) return BetValidationResult.TooLow(MIN_BET)
        if (amount > MAX_BET) return BetValidationResult.TooHigh(MAX_BET)
        
        val currentBalance = economyRepository.getCoins().first()
        if (currentBalance < amount) return BetValidationResult.InsufficientFunds
        
        return BetValidationResult.Valid
    }
}

sealed class BetValidationResult {
    object Valid : BetValidationResult()
    object InsufficientFunds : BetValidationResult()
    data class TooLow(val min: Int) : BetValidationResult()
    data class TooHigh(val max: Int) : BetValidationResult()
}
