package com.example.myapplication.domain.simulation

import com.example.myapplication.domain.model.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * THE AUTONOMOUS DECISION CENTER.
 * Uses Weighted Utility Scoring to determine the companion's next activity.
 * Implements the 70/30 Autonomy Rule.
 */
@Singleton
class BrainEngine @Inject constructor(
    private val routineManager: RoutineManager
) {

    /**
     * Evaluates the current world and internal state to produce a BuddyActivity.
     */
    fun evaluateActivity(pet: PetModel, world: WorldState, hour: Int): BuddyActivity {
        val scores = mutableMapOf<ActivityType, Float>()
        
        val scheduledActivity = routineManager.getScheduledActivity(hour)

        // 1. BASE UTILITIES
        scores[ActivityType.IDLE] = 0.2f 
        
        // 2. PERSONALITY BIASES (Temperament)
        val t = pet.psychology.temperament
        
        // 3. DYNAMIC SCORING
        
        // --- SLEEP UTILITY ---
        var sleepScore = if (pet.isSleeping) 1.0f else {
            (100f - pet.stats.energy) / 100f * (1.0f + t.laziness)
        }
        if (scheduledActivity == ActivityType.RESTING) sleepScore *= 1.5f
        scores[ActivityType.RESTING] = sleepScore.coerceIn(0f, 1f)

        // --- EXPLORATION UTILITY ---
        var exploreScore = (pet.stats.energy / 100f) * t.curiosity * (if (world.timeOfDay == TimeOfDay.DAY) 1.2f else 0.8f)
        if (scheduledActivity == ActivityType.LOOKING_AROUND) exploreScore *= 1.5f
        scores[ActivityType.LOOKING_AROUND] = exploreScore.coerceIn(0f, 1f)

        // --- SOCIAL/AFFECTION UTILITY ---
        var socialScore = (100f - pet.stats.social) / 100f * t.affection * (if (pet.stats.happiness > 50f) 1.5f else 0.5f)
        if (scheduledActivity == ActivityType.WANTING_ATTENTION) socialScore *= 1.5f
        scores[ActivityType.WANTING_ATTENTION] = socialScore.coerceIn(0f, 1f)

        // --- PLAY UTILITY ---
        var playScore = (pet.stats.energy / 100f) * (pet.stats.happiness / 100f)
        if (scheduledActivity == ActivityType.PLAYING) playScore *= 1.5f
        scores[ActivityType.PLAYING] = playScore.coerceIn(0f, 1f)

        // --- ANXIETY/HIDING UTILITY ---
        val stressScore = (pet.stats.stress / 100f) * (1.0f - t.resilience)
        if (pet.stats.hunger < 10f || pet.stats.energy < 10f) {
            scores[ActivityType.HIDING] = (stressScore * 1.5f).coerceIn(0f, 1f)
        }

        // 4. SELECT WINNER
        val winnerEntry = scores.entries.maxByOrNull { it.value }
        val winner = winnerEntry?.key ?: ActivityType.IDLE
        val confidence = winnerEntry?.value ?: 0.2f
        
        Timber.d("BrainEngine: Evaluated Activity -> ${winner.name} (Score: $confidence)")
        
        return BuddyActivity(
            type = winner,
            confidence = confidence,
            targetObjectId = determineTargetObject(winner, pet)
        )
    }

    private fun determineTargetObject(type: ActivityType, pet: PetModel): String? {
        return when (type) {
            ActivityType.LOOKING_AROUND -> {
                // Pick a bonded object to visit
                pet.psychology.objectBonds.keys.randomOrNull()
            }
            else -> null
        }
    }
}
