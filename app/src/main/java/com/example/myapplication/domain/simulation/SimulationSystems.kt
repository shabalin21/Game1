package com.example.myapplication.domain.simulation

import com.example.myapplication.core.EventBus
import com.example.myapplication.core.KernelSystem
import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.market.MarketSimulationEngine
import com.example.myapplication.domain.usecase.ProcessSimulationTickUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimulationSystems @Inject constructor(
    private val eventBus: EventBus,
    private val processSimulationTickUseCase: ProcessSimulationTickUseCase,
    private val worldManager: WorldManager,
    private val marketEngine: MarketSimulationEngine,
    private val missionManager: com.example.myapplication.domain.progression.mission.MissionManager,
    private val shopRotationManager: com.example.myapplication.domain.market.rotation.ShopRotationManager
) : KernelSystem {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onBoot() {
        Timber.i("SimulationSystems: Booting...")
        eventBus.events
            .onEach { event ->
                if (event is GameplayEvent.SimulationTick) {
                    handleTick(event.timestamp)
                }
            }
            .launchIn(scope)
    }

    override fun onShutdown() {
        Timber.i("SimulationSystems: Shutting down...")
    }

    private suspend fun handleTick(timestamp: Long) {
        processSimulationTickUseCase(timestamp)
        worldManager.tick(timestamp)
        marketEngine.tick()
        missionManager.checkAndRotateMissions()
        shopRotationManager.tick(timestamp)
    }
}
