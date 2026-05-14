package com.example.myapplication.domain.casino.engine

import com.example.myapplication.domain.casino.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class CoinFlipEngine @Inject constructor() {

    private var currentStreak = 0

    fun flip(betAmount: Int, predictedSide: CoinSide): CoinFlipResult {
        val resultSide = if (Random.nextBoolean()) CoinSide.HEADS else CoinSide.TAILS
        val isWin = resultSide == predictedSide
        
        if (isWin) {
            currentStreak++
        } else {
            currentStreak = 0
        }

        val multiplier = if (isWin) 2.0 + (currentStreak * 0.1) else 0.0
        val payout = (betAmount * multiplier).toInt()

        return CoinFlipResult(
            side = resultSide,
            isWin = isWin,
            payout = payout,
            streak = currentStreak
        )
    }

    fun resetStreak() {
        currentStreak = 0
    }
}
