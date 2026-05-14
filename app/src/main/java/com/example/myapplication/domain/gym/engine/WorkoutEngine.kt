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
        
        val newStats = mutableMapOf<String, Float>()
        val buffs = mutableListOf<Buff>()
        val logs = mutableListOf<String>()

        when (exercise.category) {
            TrainingCategory.STRENGTH -> {
                newStats["confidence"] = (stats.confidence + 2f).coerceAtMost(100f)
                newStats["discipline"] = (stats.discipline + 1.5f).coerceAtMost(100f)
                newStats["happiness"] = (stats.happiness + 1f).coerceAtMost(100f)
                newStats["energy"] = (stats.energy - 15f).coerceAtLeast(0f)
                newStats["hunger"] = (stats.hunger - 10f).coerceAtLeast(0f)
                newStats["stress"] = (stats.stress - 5f).coerceAtLeast(0f)
                
                buffs.add(Buff("str_buff", "Strength Surge", "Increased confidence and discipline.", BuffType.STRENGTH_BOOST, System.currentTimeMillis() + 3600000))
                logs.add("Hypertrophy protocols active. Muscle density increasing.")
            }
            TrainingCategory.CARDIO -> {
                newStats["stress"] = (stats.stress - 15f).coerceAtLeast(0f)
                newStats["happiness"] = (stats.happiness + 5f).coerceAtMost(100f)
                newStats["energy"] = (stats.energy - 20f).coerceAtLeast(0f)
                newStats["hunger"] = (stats.hunger - 12f).coerceAtLeast(0f)
                
                buffs.add(Buff("cardio_buff", "Endorphin Rush", "Reduced stress and improved recovery.", BuffType.CARDIO_ENDURANCE, System.currentTimeMillis() + 7200000))
                logs.add("Cardiovascular efficiency improving. Endorphin levels peaking.")
            }
            TrainingCategory.MIND_RECOVERY -> {
                newStats["emotionalStability"] = (stats.emotionalStability + 3f).coerceAtMost(100f)
                newStats["stress"] = (stats.stress - 10f).coerceAtLeast(0f)
                newStats["energy"] = (stats.energy - 5f).coerceAtLeast(0f)
                
                buffs.add(Buff("mind_buff", "Neural Focus", "Improved mental clarity and focus.", BuffType.MENTAL_CLARITY, System.currentTimeMillis() + 10800000))
                logs.add("Cortisol levels dropping. Neural networks recalibrating.")
            }
        }

        // Random chance of soreness if energy is low
        if (stats.energy < 30f && Random.nextFloat() < 0.3f) {
            buffs.add(Buff("sore_debuff", "Muscle Soreness", "Reduced physical performance.", BuffType.FATIGUE_DEBUFF, System.currentTimeMillis() + 14400000))
            logs.add("WARNING: Muscle fatigue detected. Recovery protocol recommended.")
        }

        logs.forEach { logManager.log(LogCategory.SYSTEM, "[GYM] $it") }

        return WorkoutResult(newStats, buffs, logs)
    }
}
