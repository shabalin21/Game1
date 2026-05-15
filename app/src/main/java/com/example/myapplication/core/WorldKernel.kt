package com.example.myapplication.core

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The WorldKernel is the central orchestration layer of the engine.
 * It manages the lifecycle of all core systems and ensures they are
 * initialized in the correct order.
 */
@Singleton
class WorldKernel @Inject constructor(
    eventBus: EventBus,
    canonicalState: CanonicalState,
    tickSystem: TickSystem,
    simulationSystems: com.example.myapplication.domain.simulation.SimulationSystems,
    phoneManager: com.example.myapplication.domain.simulation.phone.PhoneManager,
    lifeEventManager: com.example.myapplication.domain.simulation.lifeevents.LifeEventManager
) {
    private val systems = mutableListOf<KernelSystem>()
    private var isBooted = false

    init {
        // Define the dependency order
        systems.add(eventBus)           // 1. Messaging first
        systems.add(canonicalState)     // 2. Data model second
        systems.add(phoneManager)
        systems.add(lifeEventManager)
        systems.add(simulationSystems)  // 3. Game logic systems
        systems.add(tickSystem)         // 4. Heartbeat last
    }

    /**
     * Boot up the engine.
     */
    fun boot() {
        if (isBooted) {
            Timber.w("WorldKernel: Already booted.")
            return
        }

        Timber.i("WorldKernel: --- STARTING BOOT SEQUENCE ---")
        
        systems.forEach { system ->
            try {
                Timber.d("WorldKernel: Booting ${system.javaClass.simpleName}...")
                system.onBoot()
            } catch (e: Exception) {
                Timber.e(e, "WorldKernel: Critical failure booting ${system.javaClass.simpleName}")
            }
        }

        isBooted = true
        Timber.i("WorldKernel: --- BOOT SEQUENCE COMPLETE ---")
    }

    /**
     * Shutdown the engine.
     */
    fun shutdown() {
        if (!isBooted) return

        Timber.i("WorldKernel: --- STARTING SHUTDOWN SEQUENCE ---")
        
        // Shutdown in reverse order
        systems.reversed().forEach { system ->
            try {
                Timber.d("WorldKernel: Shutting down ${system.javaClass.simpleName}...")
                system.onShutdown()
            } catch (e: Exception) {
                Timber.e(e, "WorldKernel: Error shutting down ${system.javaClass.simpleName}")
            }
        }

        isBooted = false
        Timber.i("WorldKernel: --- SHUTDOWN SEQUENCE COMPLETE ---")
    }
}
