package com.example.myapplication.domain.event

import com.example.myapplication.domain.model.ItemCategory

sealed class GameplayEvent {
    data class FoodConsumed(val itemId: String, val hungerRestored: Float) : GameplayEvent()
    data class ToyUsed(val itemId: String, val happinessRestored: Float) : GameplayEvent()
    data class PetInteracted(val type: InteractionType) : GameplayEvent()
    data class MinigameCompleted(val gameId: String, val score: Int, val coinsEarned: Int) : GameplayEvent()
    data class EconomyTransaction(val amount: Int, val type: TransactionType, val category: String) : GameplayEvent()
    data class LevelUp(val newLevel: Int) : GameplayEvent()
    object PetSlept : GameplayEvent()
    object PetWokeUp : GameplayEvent()
    
    // World Events
    data class WorldEventTriggered(
        val id: String,
        val title: String,
        val description: String,
        val type: WorldEventType,
        val impact: WorldEventImpact = WorldEventImpact.NEUTRAL
    ) : GameplayEvent()
    
    // Casino Events
    data class CasinoGamePlayed(val gameId: String, val bet: Int) : GameplayEvent()
    data class CasinoWin(val gameId: String, val payout: Int, val isJackpot: Boolean = false) : GameplayEvent()
    data class CasinoLoss(val gameId: String, val bet: Int) : GameplayEvent()
}

enum class InteractionType {
    TAP, PET, PLAY, CLEAN, HEAL
}

enum class TransactionType {
    EARNED, SPENT
}

enum class WorldEventType {
    FINANCIAL, SOCIAL, EMOTIONAL, GAMBLING, CRYPTO, LIFESTYLE, PROPERTY, PET
}

enum class WorldEventImpact {
    POSITIVE, NEGATIVE, NEUTRAL, LEGENDARY
}
