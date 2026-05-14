package com.example.myapplication.domain.interaction

import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.event.GameplayEventManager
import com.example.myapplication.domain.model.Mood
import com.example.myapplication.domain.model.PetModel
import com.example.myapplication.domain.simulation.MoodEngine
import com.example.myapplication.domain.simulation.SimulationManager
import com.example.myapplication.ui.animation.InteractionType
import com.example.myapplication.ui.animation.JuiceEvent
import com.example.myapplication.ui.animation.JuiceManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles logic for direct player interactions with the pet.
 */
@Singleton
class PetInteractionSystem @Inject constructor(
    private val eventManager: GameplayEventManager,
    val simulationManager: SimulationManager,
    private val moodEngine: MoodEngine,
    private val juiceManager: JuiceManager
) {

    suspend fun feed(pet: PetModel, itemId: String) {
        if (simulationManager.useItem(itemId)) {
            val newEmotion = moodEngine.addModifier(
                pet.emotionState,
                Mood.EXCITED,
                0.8f,
                TimeUnit.MINUTES.toMillis(5),
                "FEED"
            )
            simulationManager.updatePetState { it.copy(emotionState = newEmotion) }
            juiceManager.triggerEffect(JuiceEvent.Interaction(InteractionType.FEED))
            eventManager.dispatchNonBlocking(GameplayEvent.FoodConsumed(itemId, 10f))
        }
    }

    suspend fun pet(pet: PetModel) {
        val newEmotion = moodEngine.addModifier(
            pet.emotionState,
            Mood.HAPPY,
            0.6f,
            TimeUnit.MINUTES.toMillis(3),
            "PET"
        )
        simulationManager.updatePetState { it.copy(emotionState = newEmotion) }
        juiceManager.triggerEffect(JuiceEvent.Interaction(InteractionType.PET))
        simulationManager.processManualPetting()
        eventManager.dispatchNonBlocking(GameplayEvent.PetInteracted(com.example.myapplication.domain.event.InteractionType.PET))
    }

    suspend fun play(pet: PetModel) {
        // Direct "Play" button now opens Minigame Hub instead of a stat boost?
        // Actually, let's keep it as a small boost if needed, but minigames are primary.
        simulationManager.processManualPetting()
        eventManager.dispatchNonBlocking(GameplayEvent.PetInteracted(com.example.myapplication.domain.event.InteractionType.PLAY))
    }

    suspend fun toggleSleep(pet: PetModel) {
        simulationManager.toggleSleep()
    }
}
