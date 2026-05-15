package com.example.myapplication.domain.gym.engine

import com.example.myapplication.domain.gym.model.*
import com.example.myapplication.domain.model.PetModel
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class WorkoutEngine @Inject constructor(
    private val logManager: TerminalLogManager
) {

    fun processWorkout(pet: PetModel, exercise: Exercise): WorkoutResult {
        val stats = pet.stats
        val psychology = pet.psychology
        val gymState = pet.gym
        
        val newStats = mutableMapOf<String, Float>()
        val buffs = mutableListOf<Buff>()
        val logs = mutableListOf<String>()

        // 1. Calculate Fatigue and Overtraining
        val baseFatigueGain = when(exercise.category) {
            TrainingCategory.STRENGTH -> 20f
            TrainingCategory.CARDIO -> 15f
            TrainingCategory.MIND_RECOVERY -> -10f
        }
        
        val overtrainingPenalty = if (gymState.fatigue > 80f) 1.5f else 1.0f
        if (gymState.fatigue > 80f) {
            logs.add("CRITICAL: Overtraining detected. Efficiency reduced. Injury risk high.")
        }

        when (exercise.category) {
            TrainingCategory.STRENGTH -> {
                newStats["confidence"] = (stats.confidence + 2f * overtrainingPenalty).coerceAtMost(100f)
                newStats["discipline"] = (stats.discipline + 1.5f).coerceAtMost(100f)
                newStats["fitness"] = (stats.fitness + 3f / overtrainingPenalty).coerceAtMost(100f)
                newStats["energy"] = (stats.energy - 15f * overtrainingPenalty).coerceAtLeast(0f)
                newStats["hunger"] = (stats.hunger - 10f).coerceAtLeast(0f)
                newStats["stress"] = (stats.stress - 5f).coerceAtLeast(0f)
                
                buffs.add(Buff("str_buff", "Strength Surge", "Increased confidence and discipline.", BuffType.STRENGTH_BOOST, System.currentTimeMillis() + 3600000))
            }
            TrainingCategory.CARDIO -> {
                newStats["stress"] = (stats.stress - 15f).coerceAtLeast(0f)
                newStats["happiness"] = (stats.happiness + 5f).coerceAtMost(100f)
                newStats["fitness"] = (stats.fitness + 2f / overtrainingPenalty).coerceAtMost(100f)
                newStats["energy"] = (stats.energy - 20f * overtrainingPenalty).coerceAtLeast(0f)
                newStats["hunger"] = (stats.hunger - 12f).coerceAtLeast(0f)
                
                buffs.add(Buff("cardio_buff", "Endorph endorphin Rush", "Reduced stress and improved recovery.", BuffType.CARDIO_ENDURANCE, System.currentTimeMillis() + 7200000))
            }
            TrainingCategory.MIND_RECOVERY -> {
                newStats["emotionalStability"] = (stats.emotionalStability + 3f).coerceAtMost(100f)
                newStats["stress"] = (stats.stress - 10f).coerceAtLeast(0f)
                newStats["energy"] = (stats.energy - 5f).coerceAtLeast(0f)
                
                buffs.add(Buff("mind_buff", "Neural Focus", "Improved mental clarity and focus.", BuffType.MENTAL_CLARITY, System.currentTimeMillis() + 10800000))
            }
        }

        // Streak and Discipline update
        val hoursSinceLast = (System.currentTimeMillis() - gymState.lastWorkoutTimestamp) / (1000f * 60 * 60)
        val isStreakMaintained = hoursSinceLast in 12f..36f
        
        if (isStreakMaintained) {
            logs.add("Streak maintained! Discipline increasing.")
        } else if (hoursSinceLast > 48f) {
            logs.add("Streak lost. Physical condition decaying.")
        }

        logs.forEach { logManager.log(LogCategory.SYSTEM, "[GYM] $it") }

        return WorkoutResult(newStats, buffs, logs)
    }
}
