package com.example.myapplication.domain.model

import com.example.myapplication.domain.repository.InventoryItem
import kotlinx.serialization.Serializable

/**
 * Unified state object representing the entire game world.
 */
data class GameState(
    val pet: PetModel? = null,
    val inventory: List<InventoryItem> = emptyList(),
    val coins: Int = 0,
    val statistics: LifetimeStats = LifetimeStats(),
    val settings: SettingsModel = SettingsModel(),
    val upgrades: List<UpgradeModel> = emptyList(),
    val world: WorldState = WorldState()
)

@Serializable
data class WorldState(
    val timeOfDay: TimeOfDay = TimeOfDay.DAY,
    val weather: Weather = Weather.SUNNY,
    val currentDistrict: District = District.RESIDENTIAL,
    val temperature: Float = 22f, // Celsius
    val cleanliness: Float = 100f, // 0 to 100
    val lastUpdateTimestamp: Long = System.currentTimeMillis()
)

@Serializable
enum class TimeOfDay {
    DAWN, DAY, DUSK, NIGHT
}

@Serializable
enum class Weather {
    SUNNY, RAINY, CLOUDY, STORMY
}

@Serializable
data class LifetimeStats(
    val totalPlayTimeMillis: Long = 0,
    val totalFoodEaten: Int = 0,
    val totalCoinsEarned: Int = 0,
    val totalCoinsSpent: Int = 0,
    val miniGamesPlayed: Int = 0,
    val interactionsCount: Int = 0,
    val maxHappinessReached: Float = 0f,
    val daysSurvived: Int = 0,
    val totalDistanceTraveled: Float = 0f,
    val sleepCyclesCompleted: Int = 0,
    // Casino Stats
    val casinoWins: Int = 0,
    val casinoLosses: Int = 0,
    val biggestCasinoWin: Int = 0,
    val casinoJackpots: Int = 0,
    // Addiction / Psychological
    val addictionIntensity: Float = 0f,
    val gamblingExposureCount: Int = 0,
    // Market Stats
    val totalInvested: Int = 0,
    val totalMarketEarned: Int = 0,
    val totalMarketProfit: Int = 0,
    val totalMarketLosses: Int = 0,
    val bestTrade: Int = 0,
    val worstTrade: Int = 0,
    val totalTrades: Int = 0,
    // Prestige System
    val prestigeMultiplier: Float = 1.0f,
    val rebirthCount: Int = 0
)

@Serializable
data class SettingsModel(
    val graphics: GraphicsSettings = GraphicsSettings(),
    val audio: AudioSettings = AudioSettings(),
    val gameplay: GameplaySettings = GameplaySettings(),
    val ui: UiSettings = UiSettings()
)

@Serializable
data class GraphicsSettings(
    val showFps: Boolean = true,
    val targetFps: Int = 60, // 30, 60, 120
    val particlesEnabled: Boolean = true,
    val animationQuality: Int = 2, // 0: Low, 1: Med, 2: High
    val lowPowerMode: Boolean = false,
    val dynamicBackgrounds: Boolean = true,
    val shadowsEnabled: Boolean = true,
    val screenShakeEnabled: Boolean = true
)

@Serializable
data class AudioSettings(
    val masterVolume: Float = 0.8f,
    val musicVolume: Float = 0.7f,
    val sfxVolume: Float = 1.0f,
    val isMuted: Boolean = false,
    val ambientEnabled: Boolean = true
)

@Serializable
data class GameplaySettings(
    val difficulty: Int = 1, // 0: Easy, 1: Normal, 2: Hard
    val autoSleepEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val idleProgressionEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val tutorialEnabled: Boolean = true
)

@Serializable
data class UiSettings(
    val darkMode: Boolean = true,
    val compactUi: Boolean = false,
    val uiScale: Float = 1.0f,
    val floatingTextEnabled: Boolean = true,
    val statAnimationEnabled: Boolean = true
)

@Serializable
data class SocialState(
    val followers: Int = 0,
    val prestige: Int = 0,
    val popularity: Int = 0,
    val posts: List<SocialPost> = emptyList()
)

@Serializable
data class SocialPost(
    val id: String,
    val content: String,
    val likes: Int,
    val timestamp: Long = System.currentTimeMillis()
)

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
