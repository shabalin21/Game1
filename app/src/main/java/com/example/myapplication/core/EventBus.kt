package com.example.myapplication.core

import com.example.myapplication.domain.event.GameplayEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized event bus for the game engine.
 * Refactored from GameplayEventManager to align with Kernel architecture.
 */
@Singleton
class EventBus @Inject constructor() : KernelSystem {
    private val _events = MutableSharedFlow<GameplayEvent>(extraBufferCapacity = 128)
    val events: SharedFlow<GameplayEvent> = _events.asSharedFlow()

    override fun onBoot() {
        Timber.i("EventBus: Booting...")
    }

    override fun onShutdown() {
        Timber.i("EventBus: Shutting down...")
    }

    suspend fun publish(event: GameplayEvent) {
        Timber.v("Publishing Event: $event")
        _events.emit(event)
    }

    fun publishNonBlocking(event: GameplayEvent) {
        Timber.v("Publishing Event (Non-blocking): $event")
        _events.tryEmit(event)
    }
}
