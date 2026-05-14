package com.example.myapplication.domain.admin

import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminCommandProcessor @Inject constructor(
    private val petRepository: PetRepository,
    private val economyRepository: EconomyRepository,
    private val logManager: TerminalLogManager
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun processCommand(command: String): Boolean {
        val parts = command.lowercase().trim().split(" ")
        if (parts.isEmpty()) return false

        when (parts[0]) {
            "sudo" -> if (parts.size > 1 && parts[1] == "override") {
                logAdminAction("ROOT OVERRIDE GRANTED")
                return true
            }
            "unlock_root" -> {
                logAdminAction("NEURAL ROOT ACCESS UNLOCKED")
                return true
            }
            "neural_admin" -> {
                logAdminAction("ADMIN INTERFACE INITIALIZED")
                return true
            }
            "godmode" -> {
                logAdminAction("REALITY PARAMETERS UNLOCKED")
                return true
            }
            "give_money" -> {
                val amount = parts.getOrNull(1)?.toIntOrNull() ?: 1000
                scope.launch { economyRepository.addCoins(amount) }
                logAdminAction("ECONOMY_BALANCE_MODIFIED: +$amount")
                return true
            }
            "set_stat" -> {
                if (parts.size >= 3) {
                    val statName = parts[1]
                    val value = parts[2].toFloatOrNull() ?: 100f
                    updateStat(statName, value)
                    return true
                }
            }
        }
        return false
    }

    private fun updateStat(name: String, value: Float) {
        scope.launch {
            val pet = petRepository.getPetState().first() ?: return@launch
            val newStats = when (name) {
                "health" -> pet.stats.copy(health = value)
                "hunger" -> pet.stats.copy(hunger = value)
                "energy" -> pet.stats.copy(energy = value)
                "happiness" -> pet.stats.copy(happiness = value)
                "stress" -> pet.stats.copy(stress = value)
                else -> pet.stats
            }
            petRepository.savePetState(pet.copy(stats = newStats))
            logAdminAction("STAT_MODIFIED: $name -> $value")
        }
    }

    private fun logAdminAction(message: String) {
        logManager.log(LogCategory.SYSTEM, "[ROOT] $message")
    }
}
