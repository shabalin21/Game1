package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class JobModel(
    val id: String,
    val name: String,
    val icon: String,
    val hourlyPay: Int,
    val stressMult: Float,
    val fatigueMult: Float,
    val minLevel: Int,
    val tier: JobTier,
    val requiredExperience: Int,
    val difficulty: Int,
    val description: String
)

@Serializable
enum class JobTier {
    ENTRY, SPECIALIST, EXPERT, ELITE, EXECUTIVE
}

@Serializable
data class EmploymentState(
    val jobId: String? = null,
    val performance: Float = 0f, // 0-100
    val experience: Int = 0,
    val totalEarned: Int = 0,
    val shiftStartTimestamp: Long = 0,
    val isWarningsIssued: Boolean = false,
    val firedCooldownTimestamp: Long = 0
)
