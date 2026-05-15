package com.example.myapplication.domain.progression.mission

import com.example.myapplication.core.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionDispatcher @Inject constructor(
    private val eventBus: EventBus,
    private val missionManager: MissionManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        eventBus.events
            .onEach { event ->
                missionManager.onGameplayEvent(event)
            }
            .launchIn(scope)
    }
}
