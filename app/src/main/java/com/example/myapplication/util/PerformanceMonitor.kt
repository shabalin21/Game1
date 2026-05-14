package com.example.myapplication.util

import android.view.Choreographer
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GENUINE FPS MONITOR.
 * Calculates real average FPS based on frame intervals.
 */
@Singleton
class PerformanceMonitor @Inject constructor() {
    private var lastFrameTimeNanos: Long = 0
    private var frameCount = 0
    private var lastCalculationTimeNanos: Long = 0

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (lastFrameTimeNanos != 0L) {
                val frameTimeMillis = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000f
                _frameTime.floatValue = frameTimeMillis
                
                frameCount++
                val timeSinceLastCalculation = frameTimeNanos - lastCalculationTimeNanos
                if (timeSinceLastCalculation >= 1_000_000_000L) { // Every 1 second
                    _fps.floatValue = (frameCount * 1_000_000_000L).toFloat() / timeSinceLastCalculation
                    frameCount = 0
                    lastCalculationTimeNanos = frameTimeNanos
                }
            } else {
                lastCalculationTimeNanos = frameTimeNanos
            }
            lastFrameTimeNanos = frameTimeNanos
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    private val _fps = mutableFloatStateOf(0f)
    val fps: State<Float> = _fps

    private val _frameTime = mutableFloatStateOf(0f)
    val frameTime: State<Float> = _frameTime

    private val _memoryUsage = mutableLongStateOf(0L)
    val memoryUsage: State<Long> = _memoryUsage

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun start() {
        Choreographer.getInstance().postFrameCallback(frameCallback)
        
        job = scope.launch {
            while (isActive) {
                val runtime = Runtime.getRuntime()
                val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
                _memoryUsage.longValue = usedMem
                delay(1000L)
            }
        }
    }

    fun stop() {
        Choreographer.getInstance().removeFrameCallback(frameCallback)
        lastFrameTimeNanos = 0
        job?.cancel()
    }
}
