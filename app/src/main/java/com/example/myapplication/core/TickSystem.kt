package com.example.myapplication.core

import com.example.myapplication.domain.event.GameplayEvent
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the core simulation heartbeat.
 * Refactored from GameLoopManager.
 */
@Singleton
class TickSystem @Inject constructor(
    private val eventBus: EventBus
) : KernelSystem {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "TickSystem: Uncaught exception in tick loop")
    }
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + exceptionHandler)
    private var job: Job? = null
    private var lastTickTimestamp: Long = 0
    
    private val TICK_RATE_MS = 2000L

    override fun onBoot() {
        Timber.i("TickSystem: Booting...")
        start()
    }

    override fun onShutdown() {
        Timber.i("TickSystem: Shutting down...")
        stop()
    }

    private fun start() {
        if (job?.isActive == true) return
        
        lastTickTimestamp = System.currentTimeMillis()
        
        job = scope.launch {
            while (isActive) {
                try {
                    val currentTime = System.currentTimeMillis()
                    lastTickTimestamp = currentTime

                    // Dispatch simulation tick event
                    eventBus.publish(GameplayEvent.SimulationTick(currentTime))
                    
                } catch (e: Exception) {
                    Timber.e(e, "Error during simulation tick")
                }
                
                delay(TICK_RATE_MS)
            }
        }
    }

    private fun stop() {
        job?.cancel()
    }
}
