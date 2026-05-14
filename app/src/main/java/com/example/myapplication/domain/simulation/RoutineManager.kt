package com.example.myapplication.domain.simulation

import com.example.myapplication.domain.model.ActivityType
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Calendar

/**
 * Manages the buddy's daily schedule and routine expectations.
 */
@Singleton
class RoutineManager @Inject constructor() {

    /**
     * Returns the ideal activity for the given hour of the day (0-23).
     */
    fun getScheduledActivity(hour: Int): ActivityType {
        return when (hour) {
            in 0..7 -> ActivityType.RESTING   // Sleep Time
            in 8..9 -> ActivityType.WANTING_ATTENTION // Morning Ritual
            in 10..12 -> ActivityType.LOOKING_AROUND // Active Exploration
            in 13..17 -> ActivityType.IDLE // Afternoon Productivity/Rest
            in 18..21 -> ActivityType.PLAYING // Evening Social/Play
            in 22..23 -> ActivityType.RESTING // Wind Down
            else -> ActivityType.IDLE
        }
    }

    /**
     * Calculates the "Routine Compliance" - how well the current state matches the schedule.
     */
    fun getRoutineBias(currentActivity: ActivityType, hour: Int): Float {
        val scheduled = getScheduledActivity(hour)
        return if (currentActivity == scheduled) 1.5f else 0.8f
    }
}
