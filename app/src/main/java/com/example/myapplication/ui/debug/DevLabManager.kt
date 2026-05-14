package com.example.myapplication.ui.debug

import androidx.compose.runtime.*
import com.example.myapplication.BuildConfig
import com.example.myapplication.data.local.DebugCheatEntity
import com.example.myapplication.data.local.PetDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevLabManager @Inject constructor(
    private val petDao: PetDao,
    private val json: Json
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var isVisible by mutableStateOf(false)
    var isActivated by mutableStateOf(false)

    // --- PLAYER CONTROL ---
    var infiniteCoins by mutableStateOf(false)
    var instantLevelUp by mutableStateOf(false)
    
    // --- STAT CONTROL ---
    var frozenStats = mutableStateMapOf<String, Float>()
    var godModeEnabled by mutableStateOf(false)
    
    // --- WORK DEBUGGING ---
    var autoMaintainPerformance by mutableStateOf(false)
    var disableFiring by mutableStateOf(false)
    var performanceFreeze by mutableStateOf(false)
    
    // --- TIME CONTROL ---
    var simulationPaused by mutableStateOf(false)
    var timeDilation by mutableFloatStateOf(1.0f)

    // --- NEW: RESOURCE INJECTION ---
    fun addXp(amount: Long) {
        log("DEBUG: Injected $amount XP")
        // Implementation will be via viewModel call in Dashboard
    }
    
    fun unlockAllJobs() {
        log("DEBUG: Unlocked all career paths")
    }

    fun skipTime(hours: Int) {
        log("DEBUG: Warped time forward by $hours hours")
    }
    
    // --- VISUAL DEBUG ---
    var particlesEnabled by mutableStateOf(true)
    var glowEnabled by mutableStateOf(true)
    var animationsEnabled by mutableStateOf(true)
    var blurEnabled by mutableStateOf(true)
    
    // --- MONITORING ---
    var showFpsCounter by mutableStateOf(false)
    var showPerformanceOverlay by mutableStateOf(false)
    var showMemoryUsage by mutableStateOf(false)
    var showRecompositions by mutableStateOf(false)
    var showActiveCoroutines by mutableStateOf(false)
    var showTickRate by mutableStateOf(false)

    private val _debugLogs = MutableStateFlow<List<String>>(emptyList())
    val debugLogs = _debugLogs.asStateFlow()

    init {
        scope.launch {
            petDao.getDebugCheats().collectLatest { entity ->
                entity?.let {
                    withContext(Dispatchers.Main) {
                        isActivated = it.isActivated
                        godModeEnabled = it.godModeEnabled
                        simulationPaused = it.simulationPaused
                        timeDilation = it.timeDilation
                        particlesEnabled = it.particlesEnabled
                        glowEnabled = it.glowEnabled
                        animationsEnabled = it.animationsEnabled
                        blurEnabled = it.blurEnabled
                        showFpsCounter = it.showFpsCounter
                        showPerformanceOverlay = it.showPerformanceOverlay
                        showMemoryUsage = it.showMemoryUsage
                        infiniteCoins = it.infiniteCoins
                        
                        try {
                            val frozen: Map<String, Float> = json.decodeFromString(it.frozenStatsJson)
                            frozenStats.clear()
                            frozenStats.putAll(frozen)
                        } catch (e: Exception) {}
                    }
                }
            }
        }
    }

    fun log(message: String) {
        val timestamp = System.currentTimeMillis() % 100000 // Short stamp
        val newLogs = (_debugLogs.value + "[$timestamp] $message").takeLast(50)
        _debugLogs.value = newLogs
    }

    fun save() {
        scope.launch {
            val frozenJson = json.encodeToString(frozenStats.toMap())
            petDao.updateDebugCheats(
                DebugCheatEntity(
                    isActivated = isActivated,
                    godModeEnabled = godModeEnabled,
                    frozenStatsJson = frozenJson,
                    simulationPaused = simulationPaused,
                    timeDilation = timeDilation,
                    particlesEnabled = particlesEnabled,
                    glowEnabled = glowEnabled,
                    animationsEnabled = animationsEnabled,
                    blurEnabled = blurEnabled,
                    showFpsCounter = showFpsCounter,
                    showPerformanceOverlay = showPerformanceOverlay,
                    showMemoryUsage = showMemoryUsage,
                    infiniteCoins = infiniteCoins
                )
            )
        }
    }

    fun activate() {
        if (BuildConfig.DEBUG) {
            isActivated = true
            isVisible = true
            save()
        }
    }

    fun toggleVisibility() {
        if (isActivated) {
            isVisible = !isVisible
        }
    }

    fun isStatFrozen(statName: String): Boolean = frozenStats.containsKey(statName)
    fun toggleFreezeStat(statName: String, value: Float) {
        if (frozenStats.containsKey(statName)) frozenStats.remove(statName) else frozenStats[statName] = value
        save()
    }

    fun toggleGodMode() { godModeEnabled = !godModeEnabled; save() }
    fun setTimeDilationValue(value: Float) { timeDilation = value; save() }
    fun toggleSimulationPause() { simulationPaused = !simulationPaused; save() }
    
    fun updateVisualToggle(type: String, enabled: Boolean) {
        when(type) {
            "particles" -> particlesEnabled = enabled
            "glow" -> glowEnabled = enabled
            "animations" -> animationsEnabled = enabled
            "blur" -> blurEnabled = enabled
            "fps" -> showFpsCounter = enabled
            "performance" -> showPerformanceOverlay = enabled
            "memory" -> showMemoryUsage = enabled
            "recompositions" -> showRecompositions = enabled
            "coroutines" -> showActiveCoroutines = enabled
            "tickrate" -> showTickRate = enabled
            "infiniteCoins" -> infiniteCoins = enabled
            "autoPerformance" -> autoMaintainPerformance = enabled
            "disableFiring" -> disableFiring = enabled
        }
        save()
    }
}
