package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MissionState(
    val dailyMissions: List<MissionProgress> = emptyList(),
    val weeklyMissions: List<MissionProgress> = emptyList(),
    val lastRotationTimestamp: Long = 0,
    val loginStreak: Int = 0,
    val lastLoginTimestamp: Long = 0
)

@Serializable
data class MissionProgress(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Float,
    val targetGoal: Float,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val type: MissionType,
    val reward: MissionReward
)

@Serializable
enum class MissionType {
    EARN_MONEY, SPEND_MONEY, WIN_CASINO, PLAY_MINIGAME, FEED_PET, INVEST_CRYPTO, BUY_ITEM
}

@Serializable
data class MissionReward(
    val coins: Int = 0,
    val xp: Int = 0,
    val items: List<String> = emptyList()
)
