package com.example.myapplication.domain.gym.model

import kotlinx.serialization.Serializable

enum class TrainingCategory {
    STRENGTH, CARDIO, MIND_RECOVERY
}

data class Exercise(
    val id: String,
    val name: String,
    val category: TrainingCategory,
    val icon: String,
    val description: String,
    val durationMillis: Long = 5000 // Simplified for simulation
)

@Serializable
data class Buff(
    val id: String,
    val name: String,
    val description: String,
    val type: BuffType,
    val endTime: Long
)

enum class BuffType {
    STRENGTH_BOOST, CARDIO_ENDURANCE, MENTAL_CLARITY, FATIGUE_DEBUFF
}

@Serializable
data class GymState(
    val fatigue: Float = 0f, // 0 to 100
    val recoveryRate: Float = 1.0f,
    val discipline: Float = 0f,
    val bodyCondition: Float = 50f,
    val workoutStreak: Int = 0,
    val lastWorkoutTimestamp: Long = 0,
    val activeBuffs: List<Buff> = emptyList()
)

data class WorkoutResult(
    val statsChanged: Map<String, Float>,
    val buffsApplied: List<Buff>,
    val logs: List<String>
)

object GymData {
    val exercises = listOf(
        // Strength
        Exercise("arm_press", "Arm Press", TrainingCategory.STRENGTH, "🦾", "Targeted bicep and tricep stimulation."),
        Exercise("leg_press", "Leg Press", TrainingCategory.STRENGTH, "🦵", "Lower body hydraulic resistance."),
        Exercise("chest_press", "Chest Press", TrainingCategory.STRENGTH, "🏋️", "Pectoral muscle fiber optimization."),
        Exercise("dumbbells", "Dumbbells", TrainingCategory.STRENGTH, "💪", "Free-weight stabilization training."),
        
        // Cardio
        Exercise("running_machine", "Running Machine", TrainingCategory.CARDIO, "🏃", "High-intensity endurance simulation."),
        Exercise("bike", "Cycling Unit", TrainingCategory.CARDIO, "🚴", "Low-impact metabolic acceleration."),
        Exercise("hiit", "HIIT Session", TrainingCategory.CARDIO, "🔥", "Extreme anaerobic conditioning."),
        
        // Mind & Recovery
        Exercise("stretching", "Stretching", TrainingCategory.MIND_RECOVERY, "🧘", "Neural-muscular flexibility protocols."),
        Exercise("breathing", "Breathing", TrainingCategory.MIND_RECOVERY, "🌬️", "Vagus nerve calming exercises."),
        Exercise("meditation", "Meditation", TrainingCategory.MIND_RECOVERY, "🧠", "Cortisol reduction and focus centering."),
        Exercise("recovery", "Cryo-Recovery", TrainingCategory.MIND_RECOVERY, "❄️", "Rapid cellular repair and cooling.")
    )
}
