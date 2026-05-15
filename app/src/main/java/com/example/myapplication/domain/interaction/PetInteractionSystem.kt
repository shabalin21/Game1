package com.example.myapplication.domain.interaction

import com.example.myapplication.domain.model.PetModel
import com.example.myapplication.domain.usecase.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles logic for direct player interactions with the pet.
 * Refactored to delegate to specialized Use Cases.
 */
@Singleton
class PetInteractionSystem @Inject constructor(
    private val feedPetUseCase: FeedPetUseCase,
    private val petThePetUseCase: PetThePetUseCase,
    private val toggleSleepUseCase: ToggleSleepUseCase,
    private val playWithPetUseCase: PlayWithPetUseCase
) {

    suspend fun feed(pet: PetModel, itemId: String) {
        feedPetUseCase(itemId)
    }

    suspend fun pet(pet: PetModel) {
        petThePetUseCase()
    }

    suspend fun play(pet: PetModel) {
        playWithPetUseCase()
    }

    suspend fun toggleSleep(pet: PetModel) {
        toggleSleepUseCase()
    }
}
