package com.example.myapplication.domain.event

import com.example.myapplication.domain.casino.model.CoinSide

sealed class GameplayIntent {
    sealed class Pet : GameplayIntent() {
        data class Feed(val itemId: String) : Pet()
        object Petting : Pet()
        object Play : Pet()
        object ToggleSleep : Pet()
    }

    sealed class Casino : GameplayIntent() {
        object TryEnter : Casino()
        data class StartBlackjack(val bet: Int) : Casino()
        object BlackjackHit : Casino()
        object BlackjackStand : Casino()
        data class PlaySlots(val bet: Int) : Casino()
        data class PlayCoinFlip(val bet: Int, val side: CoinSide) : Casino()
        data class StartCrash(val bet: Int) : Casino()
        object CashOutCrash : Casino()
        data class DropPlinkoBall(val bet: Int) : Casino()
        object PayReEntry : Casino()
    }

    sealed class Admin : GameplayIntent() {
        data class ExecuteCommand(val command: String) : Admin()
    }
}
