package com.example.myapplication.ui.animation

import com.example.myapplication.core.EventBus
import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.model.Mood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JuiceBridge @Inject constructor(
    private val eventBus: EventBus,
    private val juiceManager: JuiceManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun start() {
        eventBus.events
            .onEach { event ->
                handleEvent(event)
            }
            .launchIn(scope)
    }

    private suspend fun handleEvent(event: GameplayEvent) {
        when (event) {
            is GameplayEvent.FoodConsumed -> {
                juiceManager.triggerEffect(JuiceEvent.Interaction(InteractionType.FEED))
            }
            is GameplayEvent.PetInteracted -> {
                val juiceType = when (event.type) {
                    com.example.myapplication.domain.event.InteractionType.PET -> InteractionType.PET
                    com.example.myapplication.domain.event.InteractionType.PLAY -> InteractionType.PLAY
                    else -> null
                }
                juiceType?.let { juiceManager.triggerEffect(JuiceEvent.Interaction(it)) }
            }
            is GameplayEvent.PetSlept -> {
                juiceManager.triggerEffect(JuiceEvent.Interaction(InteractionType.SLEEP))
            }
            is GameplayEvent.LevelUp -> {
                juiceManager.triggerEffect(JuiceEvent.MoodChanged(Mood.EXCITED))
            }
            // Add more mappings as needed
            else -> {}
        }
    }
}
