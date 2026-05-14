package com.example.myapplication.ui.fx

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FXQualityManager @Inject constructor() {
    enum class QualityLevel { LOW, MEDIUM, HIGH }

    var currentQuality = QualityLevel.HIGH
        private set

    fun adjustQuality(fps: Float) {
        currentQuality = when {
            fps < 30f -> QualityLevel.LOW
            fps < 50f -> QualityLevel.MEDIUM
            else -> QualityLevel.HIGH
        }
    }

    fun getMaxParticles(): Int = when(currentQuality) {
        QualityLevel.LOW -> 50
        QualityLevel.MEDIUM -> 200
        QualityLevel.HIGH -> 500
    }
}
