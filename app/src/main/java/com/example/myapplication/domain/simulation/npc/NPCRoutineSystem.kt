package com.example.myapplication.domain.simulation.npc

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class NPCRoutineSystem @Inject constructor() {

    fun updateNPC(npc: NPCModel, hour: Int): NPCModel {
        // 1. Determine activity based on schedule (mocked for now)
        val activity = when(hour) {
            in 0..7 -> "SLEEPING"
            in 9..17 -> "WORKING"
            in 18..21 -> "SOCIALIZING"
            else -> "IDLE"
        }

        // 2. Emotional drift
        val moodShift = (Random.nextFloat() - 0.5f) * 0.1f
        
        // 3. Financial shift based on activity
        val moneyShift = when(activity) {
            "WORKING" -> 50f
            "SOCIALIZING" -> -30f
            else -> -5f
        }

        return npc.copy(
            currentActivity = activity,
            emotionalDrift = npc.emotionalDrift + moodShift,
            financialState = (npc.financialState + moneyShift).coerceAtLeast(0f)
        )
    }
}
