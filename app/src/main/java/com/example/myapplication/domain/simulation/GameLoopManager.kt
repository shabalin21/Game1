package com.example.myapplication.domain.simulation

import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.event.GameplayEventManager
import com.example.myapplication.domain.market.MarketSimulationEngine
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.repository.WorldRepository
import com.example.myapplication.domain.repository.StatisticsRepository
import com.example.myapplication.domain.state.GameStateManager
import com.example.myapplication.domain.usecase.ProcessSimulationTickUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameLoopManager @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val gameStateManager: GameStateManager,
    private val eventManager: GameplayEventManager,
    private val simulationManager: SimulationManager,
    private val processSimulationTickUseCase: ProcessSimulationTickUseCase,
    private val marketEngine: MarketSimulationEngine,
    private val worldManager: WorldManager,
    private val missionManager: com.example.myapplication.domain.progression.mission.MissionManager,
    private val shopRotationManager: com.example.myapplication.domain.market.rotation.ShopRotationManager
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "GameLoopManager: Uncaught exception in game loop")
    }
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + exceptionHandler)
    private var job: Job? = null
    private var lastTickTimestamp: Long = 0
    
    fun start() {
        if (job?.isActive == true) return
        
        Timber.i("Simulation: Starting Central Game Loop...")
        lastTickTimestamp = System.currentTimeMillis()
        
        job = scope.launch {
            while (isActive) {
                try {
                    val currentTime = System.currentTimeMillis()
                    val deltaTime = currentTime - lastTickTimestamp
                    lastTickTimestamp = currentTime

                    val currentState = gameStateManager.gameState.value
                    val pet = currentState.pet
                    
                    if (pet != null) {
                        // 1. TRIGGER SIMULATION TICK VIA USE CASE
                        processSimulationTickUseCase(currentTime)
                        
                        // 1.5 TICK WORLD MANAGER
                        worldManager.tick(currentTime)
                        
                        // 1.7 TICK MISSION MANAGER
                        missionManager.checkAndRotateMissions()
                        
                        // 1.8 TICK SHOP ROTATION
                        shopRotationManager.tick(currentTime)
                        
                        // 2. TICK MARKET ENGINE
                        marketEngine.tick()
                        
                        // 3. UPDATE PERSISTENT STATS
                        statisticsRepository.incrementPlayTime(deltaTime)
                        statisticsRepository.updateMaxHappiness(pet.stats.happiness)
                        
                        // 3. TRACK STATE TRANSITIONS (Logging)
                        val updatedPetSnapshot = gameStateManager.gameState.value.pet
                        if (updatedPetSnapshot != null && updatedPetSnapshot.isSleeping != pet.isSleeping) {
                            if (updatedPetSnapshot.isSleeping) {
                                Timber.i("Simulation: Pet entered SLEEP state.")
                                eventManager.dispatch(GameplayEvent.PetSlept)
                            } else {
                                Timber.i("Simulation: Pet entered AWAKE state.")
                                eventManager.dispatch(GameplayEvent.PetWokeUp)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error during simulation tick")
                }
                
                delay(2000L) // Efficient 2s simulation tick for passive decay
            }
        }
    }
    
    fun stop() {
        Timber.i("Stopping Game Loop...")
        job?.cancel()
    }
}
