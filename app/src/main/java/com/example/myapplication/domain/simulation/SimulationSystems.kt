package com.example.myapplication.domain.simulation

import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.event.GameplayEventManager
import com.example.myapplication.domain.market.MarketSimulationEngine
import com.example.myapplication.domain.usecase.ProcessSimulationTickUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimulationSystems @Inject constructor(
    private val eventManager: GameplayEventManager,
    private val processSimulationTickUseCase: ProcessSimulationTickUseCase,
    private val worldManager: WorldManager,
    private val marketEngine: MarketSimulationEngine,
    private val missionManager: com.example.myapplication.domain.progression.mission.MissionManager,
    private val shopRotationManager: com.example.myapplication.domain.market.rotation.ShopRotationManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start() {
        eventManager.events
            .onEach { event ->
                if (event is GameplayEvent.SimulationTick) {
                    handleTick(event.timestamp)
                }
            }
            .launchIn(scope)
    }

    private suspend fun handleTick(timestamp: Long) {
        processSimulationTickUseCase(timestamp)
        worldManager.tick(timestamp)
        marketEngine.tick()
        missionManager.checkAndRotateMissions()
        shopRotationManager.tick(timestamp)
    }
}
