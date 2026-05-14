package com.example.myapplication.domain.casino.model

// --- COMMON ---

// --- ROULETTE ---

sealed class RouletteBetType {
    object Red : RouletteBetType()
    object Black : RouletteBetType()
    data class Number(val value: Int) : RouletteBetType()
}

enum class CasinoGameType {
    ROULETTE, BLACKJACK, SLOTS, COINFLIP, CRASH, PLINKO
}

// --- BLACKJACK ---

data class Card(
    val suit: Suit,
    val rank: Rank
) {
    val value: Int get() = rank.value
    override fun toString(): String = "${rank.symbol}${suit.symbol}"
}

enum class Suit(val symbol: String) {
    SPADES("♠"), HEARTS("♥"), DIAMONDS("♦"), CLUBS("♣")
}

enum class Rank(val symbol: String, val value: Int) {
    ACE("A", 11), TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5),
    SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9), TEN("10", 10),
    JACK("J", 10), QUEEN("Q", 10), KING("K", 10)
}

sealed class BlackjackState {
    object Idle : BlackjackState()
    data class Betting(val balance: Int) : BlackjackState()
    data class Dealing(val playerHand: List<Card>, val dealerHand: List<Card>, val bet: Int) : BlackjackState()
    data class PlayerTurn(val playerHand: List<Card>, val dealerHand: List<Card>, val bet: Int, val canDouble: Boolean = false) : BlackjackState()
    data class DealerTurn(val playerHand: List<Card>, val dealerHand: List<Card>, val bet: Int) : BlackjackState()
    data class Result(val playerHand: List<Card>, val dealerHand: List<Card>, val outcome: BlackjackOutcome, val payout: Int, val bet: Int) : BlackjackState()
}

enum class BlackjackOutcome {
    WIN, BLACKJACK, LOSS, BUST, PUSH
}

// --- SLOTS ---

data class SlotSymbol(
    val id: Int,
    val name: String,
    val multiplier: Int,
    val rarity: SymbolRarity,
    val icon: String
)

enum class SymbolRarity {
    COMMON, RARE, GOLD, GLITCH, MYTHIC
}

data class SlotResult(
    val reels: List<SlotSymbol>,
    val payout: Int,
    val isJackpot: Boolean,
    val isGlitch: Boolean = false,
    val spinId: Long = System.currentTimeMillis()
)

// --- COIN FLIP ---

enum class CoinSide { HEADS, TAILS }

data class CoinFlipResult(
    val side: CoinSide,
    val isWin: Boolean,
    val payout: Int,
    val streak: Int
)

// --- CRASH ---

sealed class CrashState {
    object Idle : CrashState()
    data class Waiting(val countdown: Int) : CrashState()
    data class Rising(val multiplier: Float, val betAmount: Int) : CrashState()
    data class Crashed(val crashPoint: Float, val betAmount: Int) : CrashState()
    data class CashedOut(val cashOutMultiplier: Float, val payout: Int) : CrashState()
}

// --- PLINKO ---

enum class PlinkoRisk { LOW, MEDIUM, HIGH }

data class PlinkoBall(
    val id: Long,
    val x: Float,
    val y: Float,
    val vx: Float = 0f,
    val vy: Float = 0f,
    val isFinished: Boolean = false,
    val resultMultiplier: Float? = null
)

data class PlinkoState(
    val balls: List<PlinkoBall> = emptyList(),
    val activeRisk: PlinkoRisk = PlinkoRisk.MEDIUM,
    val recentPayouts: List<Float> = emptyList()
)
