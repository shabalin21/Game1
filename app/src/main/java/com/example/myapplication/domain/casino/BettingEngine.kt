package com.example.myapplication.domain.casino

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class BettingEngine @Inject constructor() {

    // --- ROULETTE ---
    
    fun spinRoulette(): Int = Random.nextInt(0, 37) // 0-36

    fun calculateRoulettePayout(betAmount: Int, betType: RouletteBetType, result: Int): Int {
        return when (betType) {
            is RouletteBetType.Number -> if (result == betType.value) betAmount * 35 else 0
            RouletteBetType.Red -> if (isRed(result)) betAmount * 2 else 0
            RouletteBetType.Black -> if (isBlack(result)) betAmount * 2 else 0
            RouletteBetType.Even -> if (result != 0 && result % 2 == 0) betAmount * 2 else 0
            RouletteBetType.Odd -> if (result != 0 && result % 2 != 0) betAmount * 2 else 0
        }
    }

    private fun isRed(n: Int): Boolean {
        val red = listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
        return n in red
    }

    private fun isBlack(n: Int): Boolean = n != 0 && !isRed(n)

    // --- BLACKJACK ---

    fun drawCard(): Int = Random.nextInt(1, 14) // 1-13 (1=Ace, 11-13=Face)

    fun getCardValue(card: Int, currentTotal: Int): Int {
        return when (card) {
            1 -> if (currentTotal + 11 <= 21) 11 else 1 // Ace logic
            in 11..13 -> 10 // Face cards
            else -> card
        }
    }

    // --- SLOTS ---

    fun spinSlots(): List<Int> {
        return listOf(Random.nextInt(0, 8), Random.nextInt(0, 8), Random.nextInt(0, 8))
    }

    fun calculateSlotPayout(betAmount: Int, result: List<Int>): Int {
        if (result[0] == result[1] && result[1] == result[2]) {
            return when (result[0]) {
                7 -> betAmount * 50 // Jackpot
                6 -> betAmount * 20
                else -> betAmount * 10
            }
        }
        if (result[0] == result[1] || result[1] == result[2]) {
            return betAmount * 2 // Near win/Partial
        }
        return 0
    }
}

sealed class RouletteBetType {
    data class Number(val value: Int) : RouletteBetType()
    object Red : RouletteBetType()
    object Black : RouletteBetType()
    object Even : RouletteBetType()
    object Odd : RouletteBetType()
}
