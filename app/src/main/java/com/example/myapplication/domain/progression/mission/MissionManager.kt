package com.example.myapplication.domain.progression.mission

import com.example.myapplication.core.EventBus
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.event.GameplayEvent
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import java.util.*
import kotlin.random.Random

@Singleton
class MissionManager @Inject constructor(
    private val petRepository: PetRepository,
    private val eventBus: EventBus
) {
    suspend fun checkAndRotateMissions() {
        val pet = petRepository.getPetState().first() ?: return
        val currentTime = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L

        if (currentTime - pet.missions.lastRotationTimestamp > dayInMillis) {
            rotateMissions(pet, currentTime, dayInMillis)
        }
    }

    private suspend fun rotateMissions(pet: PetModel, currentTime: Long, dayInMillis: Long) {
        Timber.i("Missions: Rotating daily and weekly objectives")
        
        val newDailies = listOf(
            generateRandomMission(MissionType.EARN_MONEY, false),
            generateRandomMission(MissionType.PLAY_MINIGAME, false),
            generateRandomMission(MissionType.FEED_PET, false)
        )
        
        val newWeeklies = if (currentTime - pet.missions.lastRotationTimestamp > dayInMillis * 7) {
            listOf(
                generateRandomMission(MissionType.WIN_CASINO, true),
                generateRandomMission(MissionType.INVEST_CRYPTO, true)
            )
        } else pet.missions.weeklyMissions

        val updatedMissions = pet.missions.copy(
            dailyMissions = newDailies,
            weeklyMissions = newWeeklies,
            lastRotationTimestamp = currentTime
        )
        
        petRepository.savePetState(pet.copy(missions = updatedMissions))
    }

    private fun generateRandomMission(type: MissionType, isWeekly: Boolean): MissionProgress {
        val multiplier = if (isWeekly) 5f else 1f
        return when (type) {
            MissionType.EARN_MONEY -> MissionProgress(
                id = UUID.randomUUID().toString(),
                title = "Wealth Accumulator",
                description = "Earn ${ (1000 * multiplier).toInt() } credits",
                currentProgress = 0f,
                targetGoal = 1000f * multiplier,
                type = type,
                reward = MissionReward(coins = (200 * multiplier).toInt(), xp = (50 * multiplier).toInt())
            )
            MissionType.FEED_PET -> MissionProgress(
                id = UUID.randomUUID().toString(),
                title = "Gourmet Provider",
                description = "Feed Buddy ${ (3 * multiplier).toInt() } times",
                currentProgress = 0f,
                targetGoal = 3f * multiplier,
                type = type,
                reward = MissionReward(coins = (100 * multiplier).toInt(), xp = (30 * multiplier).toInt())
            )
            else -> MissionProgress(
                id = UUID.randomUUID().toString(),
                title = "Active Citizen",
                description = "Complete objectives in the city",
                currentProgress = 0f,
                targetGoal = 5f * multiplier,
                type = type,
                reward = MissionReward(coins = (150 * multiplier).toInt(), xp = (40 * multiplier).toInt())
            )
        }
    }

    suspend fun onGameplayEvent(event: GameplayEvent) {
        val pet = petRepository.getPetState().first() ?: return
        var missionsChanged = false
        
        val updatedDailies = pet.missions.dailyMissions.map { mission ->
            val progress = calculateProgress(mission, event)
            if (progress > 0) {
                missionsChanged = true
                val newProgress = (mission.currentProgress + progress).coerceAtMost(mission.targetGoal)
                mission.copy(currentProgress = newProgress, isCompleted = newProgress >= mission.targetGoal)
            } else mission
        }

        val updatedWeeklies = pet.missions.weeklyMissions.map { mission ->
            val progress = calculateProgress(mission, event)
            if (progress > 0) {
                missionsChanged = true
                val newProgress = (mission.currentProgress + progress).coerceAtMost(mission.targetGoal)
                mission.copy(currentProgress = newProgress, isCompleted = newProgress >= mission.targetGoal)
            } else mission
        }

        if (missionsChanged) {
            petRepository.savePetState(pet.copy(
                missions = pet.missions.copy(
                    dailyMissions = updatedDailies,
                    weeklyMissions = updatedWeeklies
                )
            ))
        }
    }

    private fun calculateProgress(mission: MissionProgress, event: GameplayEvent): Float {
        return when (mission.type) {
            MissionType.EARN_MONEY -> if (event is GameplayEvent.EconomyTransaction && event.type == com.example.myapplication.domain.event.TransactionType.EARNED) event.amount.toFloat() else 0f
            MissionType.FEED_PET -> if (event is GameplayEvent.FoodConsumed) 1f else 0f
            MissionType.PLAY_MINIGAME -> if (event is GameplayEvent.MinigameCompleted) 1f else 0f
            MissionType.WIN_CASINO -> if (event is GameplayEvent.CasinoWin) 1f else 0f
            else -> 0f
        }
    }
}
