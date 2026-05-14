package com.example.myapplication.domain.casino.engine

import com.example.myapplication.domain.casino.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SlotMachineEngine @Inject constructor() {

    private val symbols = listOf(
        SlotSymbol(1, "CHERRY", 2, SymbolRarity.COMMON, "🍒"),
        SlotSymbol(2, "LEMON", 3, SymbolRarity.COMMON, "🍋"),
        SlotSymbol(3, "GRAPE", 5, SymbolRarity.COMMON, "🍇"),
        SlotSymbol(4, "BELL", 10, SymbolRarity.RARE, "🔔"),
        SlotSymbol(5, "DIAMOND", 25, SymbolRarity.RARE, "💎"),
        SlotSymbol(6, "SEVEN", 50, SymbolRarity.GOLD, "7️⃣"),
        SlotSymbol(7, "GLITCH", 100, SymbolRarity.GLITCH, "👾"),
        SlotSymbol(8, "CROWN", 500, SymbolRarity.MYTHIC, "👑")
    )

    fun spin(betAmount: Int): SlotResult {
        val resultReels = mutableListOf<SlotSymbol>()
        repeat(3) {
            resultReels.add(getRandomSymbol())
        }

        val payout = calculatePayout(betAmount, resultReels)
        val isJackpot = payout >= betAmount * 50
        val isGlitch = resultReels.any { it.rarity == SymbolRarity.GLITCH }

        return SlotResult(
            reels = resultReels,
            payout = payout,
            isJackpot = isJackpot,
            isGlitch = isGlitch
        )
    }

    private fun getRandomSymbol(): SlotSymbol {
        val rand = Random.nextInt(1000)
        return when {
            rand < 5 -> symbols.find { it.rarity == SymbolRarity.MYTHIC }!! // 0.5%
            rand < 25 -> symbols.find { it.rarity == SymbolRarity.GLITCH }!! // 2%
            rand < 75 -> symbols.find { it.rarity == SymbolRarity.GOLD }!! // 5%
            rand < 200 -> symbols.filter { it.rarity == SymbolRarity.RARE }.random() // 12.5%
            else -> symbols.filter { it.rarity == SymbolRarity.COMMON }.random() // ~80%
        }
    }

    private fun calculatePayout(bet: Int, reels: List<SlotSymbol>): Int {
        // All three match
        if (reels[0].id == reels[1].id && reels[1].id == reels[2].id) {
            return bet * reels[0].multiplier
        }
        
        // Two match (Partial win)
        if (reels[0].id == reels[1].id || reels[1].id == reels[2].id || reels[0].id == reels[2].id) {
            val matchingSymbol = if (reels[0].id == reels[1].id) reels[0] else reels[2]
            return (bet * matchingSymbol.multiplier * 0.2).toInt()
        }

        // Glitch bonus
        if (reels.any { it.rarity == SymbolRarity.GLITCH }) {
            return if (Random.nextFloat() < 0.1f) bet * 5 else 0
        }

        return 0
    }
}
