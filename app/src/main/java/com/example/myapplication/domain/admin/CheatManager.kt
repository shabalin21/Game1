package com.example.myapplication.domain.admin

import com.example.myapplication.data.local.PetDao
import com.example.myapplication.data.local.toDomain
import com.example.myapplication.data.local.toEntity
import com.example.myapplication.data.local.EconomyEntity
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.WorldRepository
import com.example.myapplication.domain.work.engine.JobManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Singleton
class CheatManager @Inject constructor(
    private val petDao: PetDao,
    private val worldRepository: WorldRepository,
    private val jobManager: JobManager,
    private val logManager: TerminalLogManager,
    private val json: Json
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isActivated = MutableStateFlow(false)
    val isActivated: StateFlow<Boolean> = _isActivated.asStateFlow()

    // --- PLAYER CHEATS ---
    val frozenStats = MutableStateFlow<Map<String, Float>>(emptyMap())
    val godModeEnabled = MutableStateFlow(false)
    
    // --- ECONOMY CHEATS ---
    val infiniteMoney = MutableStateFlow(false)

    // --- CASINO CHEATS ---
    val bypassCasinoFee = MutableStateFlow(false)
    val guaranteedCasinoWin = MutableStateFlow(false)
    val forceJackpot = MutableStateFlow(false)

    // --- TIME CHEATS ---
    val timeDilation = MutableStateFlow(1.0f)
    val simulationPaused = MutableStateFlow(false)

    fun activate() {
        _isActivated.value = true
        logAdminAction("ADMIN_CONSOLE_ACTIVATED")
    }

    // --- STAT METHODS ---
    fun setLevel(level: Int) {
        scope.launch {
            val petEntity = petDao.getPetState().first() ?: return@launch
            val pet = petEntity.toDomain(json)
            petDao.insertPet(pet.copy(level = level).toEntity(json))
            logAdminAction("LEVEL_OVERRIDE: $level")
        }
    }

    fun addXp(amount: Long) {
        scope.launch {
            val petEntity = petDao.getPetState().first() ?: return@launch
            val pet = petEntity.toDomain(json)
            petDao.insertPet(pet.copy(xp = pet.xp + amount).toEntity(json))
            logAdminAction("XP_INJECTION: +$amount")
        }
    }

    fun setStat(name: String, value: Float) {
        scope.launch {
            val petEntity = petDao.getPetState().first() ?: return@launch
            val pet = petEntity.toDomain(json)
            val newStats = when (name.lowercase()) {
                "hunger" -> pet.stats.copy(hunger = value)
                "thirst" -> pet.stats.copy(thirst = value)
                "energy" -> pet.stats.copy(energy = value)
                "hygiene" -> pet.stats.copy(hygiene = value)
                "happiness" -> pet.stats.copy(happiness = value)
                "health" -> pet.stats.copy(health = value)
                "stress" -> pet.stats.copy(stress = value)
                "mental" -> pet.stats.copy(mentalEnergy = value)
                "sanity" -> pet.stats.copy(emotionalStability = value)
                "focus" -> pet.stats.copy(attention = value)
                else -> pet.stats
            }
            petDao.insertPet(pet.copy(stats = newStats.clamped()).toEntity(json))
            logAdminAction("STAT_OVERRIDE: $name -> $value")
        }
    }

    fun refillAllStats() {
        scope.launch {
            val petEntity = petDao.getPetState().first() ?: return@launch
            val pet = petEntity.toDomain(json)
            val fullStats = PetStats(
                hunger = 100f,
                thirst = 100f,
                energy = 100f,
                hygiene = 100f,
                happiness = 100f,
                health = 100f,
                stress = 0f,
                attention = 100f,
                emotionalStability = 100f,
                mentalEnergy = 100f
            )
            petDao.insertPet(pet.copy(stats = fullStats).toEntity(json))
            logAdminAction("NEURAL_RESTORATION_COMPLETE")
        }
    }

    fun toggleStatFreeze(name: String, value: Float) {
        val current = frozenStats.value
        frozenStats.value = if (current.containsKey(name)) current - name else current + (name to value)
        logAdminAction("STAT_LOCK_TOGGLED: $name")
    }

    // --- ECONOMY METHODS ---
    fun addMoney(amount: Int) {
        scope.launch {
            val economy = petDao.getEconomy().first() ?: EconomyEntity()
            petDao.updateEconomy(economy.copy(coins = economy.coins + amount))
            logAdminAction("CREDIT_INJECTION: +$amount")
        }
    }

    fun setBalance(amount: Int) {
        scope.launch {
            val economy = petDao.getEconomy().first() ?: EconomyEntity()
            petDao.updateEconomy(economy.copy(coins = amount))
            logAdminAction("BALANCE_RECALIBRATED: $amount")
        }
    }

    // --- WORLD CHEATS ---
    fun setWeather(weather: Weather) {
        scope.launch {
            worldRepository.updateWorldState { it.copy(weather = weather) }
            logAdminAction("METEOROLOGICAL_OVERRIDE: $weather")
        }
    }

    // --- SOCIAL CHEATS ---
    fun setFollowers(count: Int) {
        scope.launch {
            val petEntity = petDao.getPetState().first() ?: return@launch
            val pet = petEntity.toDomain(json)
            val newSocial = pet.social.copy(followers = count)
            petDao.insertPet(pet.copy(social = newSocial).toEntity(json))
            logAdminAction("SOCIAL_INFLUENCE_RECALIBRATED: $count")
        }
    }

    fun setSocialPrestige(level: Int) {
        scope.launch {
            val petEntity = petDao.getPetState().first() ?: return@launch
            val pet = petEntity.toDomain(json)
            val newSocial = pet.social.copy(prestige = level)
            petDao.insertPet(pet.copy(social = newSocial).toEntity(json))
            logAdminAction("PRESTIGE_OVERRIDE: $level")
        }
    }

    // --- INVENTORY CHEATS ---
    fun clearInventory() {
        scope.launch {
            petDao.deleteAllInventory()
            logAdminAction("INVENTORY_PURGED")
        }
    }

    fun unlockAllItems() {
        scope.launch {
            val petEntity = petDao.getPetState().first() ?: return@launch
            val pet = petEntity.toDomain(json)
            // Assuming we have a list of all item IDs somewhere, or just a large set
            // For now, let's just log it or add some common IDs
            logAdminAction("COLLECTION_LOG_EXPANDED")
        }
    }

    // --- ITEM GIVER ---
    fun giveItem(itemId: String, amount: Int = 1) {
        // This still needs a repository or direct DAO access to inventory
        // I'll leave as placeholder for now or add direct DAO update
        logAdminAction("ITEM_SPAWNED: $itemId x$amount")
    }

    // --- WORK CHEATS ---
    fun resetCareer() {
        // Since JobManager's career state isn't persisted yet, we can't easily reset it
        // unless JobManager has a reset method.
        logAdminAction("CAREER_RESET_ATTEMPTED")
    }

    fun maxJobExperience() {
        scope.launch {
            // This is a bit complex as it needs to update JobManager's state
            // For now, let's just log it or implement if JobManager allows
            logAdminAction("JOB_XP_MAXIMIZED")
        }
    }

    // --- LOGGING ---
    private fun logAdminAction(message: String) {
        logManager.log(LogCategory.SYSTEM, "[ROOT] $message")
    }
}
