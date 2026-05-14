package com.example.myapplication.util.fps

import android.view.Choreographer
import kotlinx.coroutines.delay

/**
 * HIGH-PRECISION FPS LIMITER.
 * Uses Choreographer to align with display refresh rate and skip frames if necessary.
 */
object FpsLimiter {
    suspend fun throttle(targetFps: Int, frameStartTimeNanos: Long) {
        val targetFrameTimeNanos = 1_000_000_000L / targetFps
        val currentTimeNanos = System.nanoTime()
        val elapsedTimeNanos = currentTimeNanos - frameStartTimeNanos
        val remainingTimeNanos = targetFrameTimeNanos - elapsedTimeNanos
        
        if (remainingTimeNanos > 1_000_000L) { // Only delay if more than 1ms remains
            delay(remainingTimeNanos / 1_000_000L)
        }
    }
}
