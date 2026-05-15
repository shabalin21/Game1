package com.example.myapplication.domain.simulation.lifeevents

import com.example.myapplication.core.EventBus
import com.example.myapplication.core.KernelSystem
import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.*
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class LifeEventManager @Inject constructor(
    private val eventBus: EventBus,
    private val petRepository: PetRepository,
    private val economyRepository: EconomyRepository,
    private val logManager: TerminalLogManager
) : KernelSystem {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onBoot() {
        Timber.i("LifeEventManager: Booting...")
        eventBus.events
            .onEach { event ->
                if (event is GameplayEvent.SimulationTick) {
                    handleSimulationTick()
                }
            }
            .launchIn(scope)
    }

    override fun onShutdown() {
        Timber.i("LifeEventManager: Shutting down...")
    }

    private suspend fun handleSimulationTick() {
        LifeEventRegistry.allEvents.forEach { event ->
            if (Random.nextFloat() < event.probability) {
                triggerEvent(event)
            }
        }
    }

    private suspend fun triggerEvent(event: LifeEvent) {
        val pet = petRepository.getPetState().first() ?: return
        
        logManager.log(LogCategory.SYSTEM, "LIFE_EVENT: ${event.title} - ${event.description}")
        
        // Apply effects
        if (event.effect.coinDelta != 0) {
            if (event.effect.coinDelta > 0) {
                economyRepository.addCoins(event.effect.coinDelta)
            } else {
                economyRepository.spendCoins(-event.effect.coinDelta)
            }
        }

        val updatedStats = pet.stats.applyEffect(event.effect.statEffect)
        
        var updatedEmotion = pet.emotionState
        event.effect.moodModifier?.let { mod ->
            updatedEmotion = updatedEmotion.copy(
                modifiers = updatedEmotion.modifiers + mod
            )
        }

        petRepository.savePetState(pet.copy(
            stats = updatedStats,
            emotionState = updatedEmotion
        ))
    }
}
