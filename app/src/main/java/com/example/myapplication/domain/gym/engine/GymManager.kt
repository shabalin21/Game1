package com.example.myapplication.domain.gym.engine

import com.example.myapplication.domain.gym.model.*
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymManager @Inject constructor(
    private val petRepository: PetRepository,
    private val workoutEngine: WorkoutEngine,
    private val logManager: TerminalLogManager
) {

    suspend fun train(exercise: Exercise) {
        val pet = petRepository.getPetState().first() ?: return
        
        if (pet.stats.energy < 10f) {
            logManager.log(LogCategory.WARNING, "INSUFFICIENT_ENERGY: Training aborted.")
            return
        }

        logManager.log(LogCategory.SYSTEM, "Initiating ${exercise.name} session...")
        
        val result = workoutEngine.processWorkout(pet, exercise)
        
        val updatedStats = pet.stats.copy(
            hunger = result.statsChanged["hunger"] ?: pet.stats.hunger,
            energy = result.statsChanged["energy"] ?: pet.stats.energy,
            happiness = result.statsChanged["happiness"] ?: pet.stats.happiness,
            stress = result.statsChanged["stress"] ?: pet.stats.stress,
            confidence = result.statsChanged["confidence"] ?: pet.stats.confidence,
            discipline = result.statsChanged["discipline"] ?: pet.stats.discipline,
            fitness = result.statsChanged["fitness"] ?: pet.stats.fitness,
            emotionalStability = result.statsChanged["emotionalStability"] ?: pet.stats.emotionalStability
        )

        // Task 4: Update Gym State
        val hoursSinceLast = (System.currentTimeMillis() - pet.gym.lastWorkoutTimestamp) / (1000f * 60 * 60)
        val isStreakMaintained = hoursSinceLast in 12f..36f
        
        val newStreak = if (isStreakMaintained) pet.gym.workoutStreak + 1 else if (hoursSinceLast > 48f) 0 else pet.gym.workoutStreak
        val fatigueGain = when(exercise.category) {
            TrainingCategory.STRENGTH -> 20f
            TrainingCategory.CARDIO -> 15f
            TrainingCategory.MIND_RECOVERY -> -10f
        }

        val updatedGym = pet.gym.copy(
            fatigue = (pet.gym.fatigue + fatigueGain).coerceIn(0f, 100f),
            workoutStreak = newStreak,
            lastWorkoutTimestamp = System.currentTimeMillis(),
            discipline = (pet.gym.discipline + 0.5f + (newStreak * 0.1f)).coerceAtMost(100f)
        )

        petRepository.savePetState(pet.copy(stats = updatedStats.clamped(), gym = updatedGym))
    }
}
