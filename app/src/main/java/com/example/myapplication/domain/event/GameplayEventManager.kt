package com.example.myapplication.domain.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameplayEventManager @Inject constructor() {
    private val _events = MutableSharedFlow<GameplayEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<GameplayEvent> = _events.asSharedFlow()

    suspend fun dispatch(event: GameplayEvent) {
        Timber.d("Dispatching GameplayEvent: $event")
        _events.emit(event)
    }

    fun dispatchNonBlocking(event: GameplayEvent) {
        Timber.d("Dispatching GameplayEvent (Non-blocking): $event")
        _events.tryEmit(event)
    }
}
