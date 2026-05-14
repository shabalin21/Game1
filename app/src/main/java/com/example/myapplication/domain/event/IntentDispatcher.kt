package com.example.myapplication.domain.event

import com.example.myapplication.domain.interaction.PetInteractionSystem
import com.example.myapplication.domain.casino.CasinoManager
import com.example.myapplication.domain.admin.AdminCommandProcessor
import com.example.myapplication.domain.repository.PetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntentDispatcher @Inject constructor(
    private val petInteractionSystem: PetInteractionSystem,
    private val casinoManager: CasinoManager,
    private val adminCommandProcessor: AdminCommandProcessor,
    private val petRepository: PetRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun dispatch(intent: GameplayIntent) {
        Timber.d("Dispatching Intent: $intent")
        scope.launch {
            when (intent) {
                is GameplayIntent.Pet -> handlePetIntent(intent)
                is GameplayIntent.Casino -> handleCasinoIntent(intent)
                is GameplayIntent.Admin -> handleAdminIntent(intent)
            }
        }
    }

    private suspend fun handlePetIntent(intent: GameplayIntent.Pet) {
        val pet = petRepository.getPetState().firstOrNull() ?: return
        when (intent) {
            is GameplayIntent.Pet.Feed -> petInteractionSystem.feed(pet, intent.itemId)
            is GameplayIntent.Pet.Petting -> petInteractionSystem.pet(pet)
            is GameplayIntent.Pet.Play -> petInteractionSystem.play(pet)
            is GameplayIntent.Pet.ToggleSleep -> petInteractionSystem.toggleSleep(pet)
        }
    }

    private suspend fun handleCasinoIntent(intent: GameplayIntent.Casino) {
        when (intent) {
            is GameplayIntent.Casino.TryEnter -> casinoManager.tryEnterCasino()
            is GameplayIntent.Casino.StartBlackjack -> casinoManager.startBlackjack(intent.bet)
            is GameplayIntent.Casino.BlackjackHit -> casinoManager.blackjackHit()
            is GameplayIntent.Casino.BlackjackStand -> casinoManager.blackjackStand()
            is GameplayIntent.Casino.PlaySlots -> casinoManager.playSlots(intent.bet)
            is GameplayIntent.Casino.PlayCoinFlip -> casinoManager.playCoinFlip(intent.bet, intent.side)
            is GameplayIntent.Casino.StartCrash -> casinoManager.startCrash(intent.bet)
            is GameplayIntent.Casino.CashOutCrash -> casinoManager.cashOutCrash()
            is GameplayIntent.Casino.DropPlinkoBall -> casinoManager.dropPlinkoBall(intent.bet)
            is GameplayIntent.Casino.PayReEntry -> { /* CasinoManager doesn't have a direct payReEntry, but we can call tryEnter or implement it */ }
        }
    }

    private fun handleAdminIntent(intent: GameplayIntent.Admin) {
        when (intent) {
            is GameplayIntent.Admin.ExecuteCommand -> adminCommandProcessor.processCommand(intent.command)
        }
    }
}
