package com.example.myapplication.domain.usecase

import com.example.myapplication.core.EventBus
import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.event.InteractionType
import com.example.myapplication.domain.model.Mood
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.simulation.MoodEngine
import com.example.myapplication.domain.simulation.SimulationManager
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedPetUseCase @Inject constructor(
    private val petRepository: PetRepository,
    private val simulationManager: SimulationManager,
    private val moodEngine: MoodEngine,
    private val eventBus: EventBus
) {
    suspend operator fun invoke(itemId: String): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        if (simulationManager.useItem(itemId)) {
            val newEmotion = moodEngine.addModifier(
                pet.emotionState,
                Mood.EXCITED,
                0.8f,
                TimeUnit.MINUTES.toMillis(5),
                "FEED"
            )
            simulationManager.updatePetState { it.copy(emotionState = newEmotion) }
            eventBus.publishNonBlocking(GameplayEvent.FoodConsumed(itemId, 10f))
            return true
        }
        return false
    }
}

@Singleton
class PetThePetUseCase @Inject constructor(
    private val petRepository: PetRepository,
    private val simulationManager: SimulationManager,
    private val moodEngine: MoodEngine,
    private val eventBus: EventBus
) {
    suspend operator fun invoke(): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        val mood = pet.emotionState.primaryMood
        val bond = pet.stats.bond
        
        var moodToApply = Mood.HAPPY
        var intensity = 0.6f
        var duration = 3L
        var happinessGain = 8f
        var trustGain = 3f

        when {
            mood == Mood.ANGRY -> {
                if (Math.random() < 0.4) {
                    moodToApply = Mood.ANGRY
                    intensity = 0.4f
                    duration = 2L
                    happinessGain = -5f
                    trustGain = -2f
                } else {
                    moodToApply = Mood.RELAXED
                    intensity = 0.2f
                    duration = 1L
                    happinessGain = 2f
                    trustGain = 1f
                }
            }
            mood == Mood.SAD -> {
                moodToApply = Mood.HAPPY
                intensity = 0.3f
                duration = 5L
                happinessGain = 10f
                trustGain = 5f
            }
            bond > 80f -> {
                moodToApply = Mood.EXCITED
                intensity = 0.9f
                duration = 10L
                happinessGain = 15f
                trustGain = 10f
            }
            else -> {
                // Default Happy
            }
        }

        val newEmotion = moodEngine.addModifier(
            pet.emotionState,
            moodToApply,
            intensity,
            TimeUnit.MINUTES.toMillis(duration),
            "PET"
        )
        
        simulationManager.updatePetState { 
            it.copy(
                emotionState = newEmotion,
                stats = it.stats.copy(
                    happiness = (it.stats.happiness + happinessGain).coerceIn(0f, 100f),
                    trust = (it.stats.trust + trustGain).coerceIn(0f, 100f),
                    social = (it.stats.social + 10f).coerceAtMost(100f)
                ).clamped()
            )
        }

        eventBus.publishNonBlocking(GameplayEvent.PetInteracted(InteractionType.PET))
        return true
    }
}

@Singleton
class ToggleSleepUseCase @Inject constructor(
    private val simulationManager: SimulationManager
) {
    suspend operator fun invoke() {
        simulationManager.toggleSleep()
    }
}

@Singleton
class PlayWithPetUseCase @Inject constructor(
    private val simulationManager: SimulationManager,
    private val eventBus: EventBus
) {
    suspend operator fun invoke() {
        simulationManager.processManualPetting()
        eventBus.publishNonBlocking(GameplayEvent.PetInteracted(InteractionType.PLAY))
    }
}
