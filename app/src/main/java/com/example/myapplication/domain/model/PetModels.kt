package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.math.roundToInt

/**
 * Main domain model for the creature.
 * Refactored to support permanent ownership and equipment.
 */
@Serializable
data class PetModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Buddy",
    val stats: PetStats = PetStats(),
    val birthTimestamp: Long = System.currentTimeMillis(),
    val lastUpdateTimestamp: Long = System.currentTimeMillis(),
    val level: Int = 1,
    val xp: Long = 0,
    val lifestylePoints: Long = 0,
    val isSleeping: Boolean = false,
    val psychology: PsychologyState = PsychologyState(),
    val emotionState: EmotionState = EmotionState(),
    val evolutionStage: EvolutionStage = EvolutionStage.BABY,
    val lifetimeStats: LifetimeStats = LifetimeStats(),
    val appearance: BuddyAppearance = BuddyAppearance(),
    val social: SocialState = SocialState(),
    val missions: MissionState = MissionState(),
    val collectionLog: Set<String> = emptySet(), // Set of item IDs discovered
    
    // NEW: Emotion Memory Graph
    val memoryGraph: EmotionMemoryGraph = EmotionMemoryGraph(),
    
    // NEW: Equipment System
    val equippedItems: Map<ItemCategory, String> = emptyMap(),
    val savedOutfits: Map<String, Map<ItemCategory, String>> = emptyMap(), // Name to equipped map
    
    // NEW: Permanent Ownership (for non-consumables)
    val ownedPermanentIds: Set<String> = emptySet(),
    
    // NEW: Asset System (Persistent BTC etc)
    val ownedAssets: Map<String, Float> = emptyMap(),
    val assetCostBasis: Map<String, Float> = emptyMap(),
    
    // vNext: Work System
    val employment: EmploymentState = EmploymentState(),
    
    // vNext: Buff System
    val activeModifiers: List<TimedModifier> = emptyList(),
    val modifiers: List<com.example.myapplication.core.modifier.Modifier> = emptyList(),
    val casinoSession: CasinoSession = CasinoSession(),
    val phone: PhoneState = PhoneState(),
    val gym: com.example.myapplication.domain.gym.model.GymState = com.example.myapplication.domain.gym.model.GymState()
)

@Serializable
data class CasinoSession(
    val sessionWins: Int = 0,
    val isBanned: Boolean = false,
    val banExpiryTimestamp: Long = 0,
    val reEntryFee: Int = 500
)

@Serializable
data class TimedModifier(
    val id: String,
    val name: String,
    val effect: StatEffect,
    val expirationTimestamp: Long,
    val icon: String = "⚡",
    val sideEffect: StatEffect? = null
)

@Serializable
enum class EvolutionStage {
    EGG, BABY, CHILD, TEEN, ADULT, SENIOR
}

/**
 * Represents the 16 core stats of the creature simulation.
 * Values are normalized between 0.0 and 100.0.
 */
@Serializable
data class PetStats(
    val hunger: Float = 100f,
    val thirst: Float = 100f,
    val energy: Float = 100f,
    val hygiene: Float = 100f,
    val happiness: Float = 100f,
    val health: Float = 100f,
    val stress: Float = 0f,
    val social: Float = 50f,
    val intelligence: Float = 0f,
    val discipline: Float = 0f,
    val confidence: Float = 50f,
    val curiosity: Float = 50f,
    val comfort: Float = 100f,
    val fitness: Float = 50f,
    val attention: Float = 100f,
    val emotionalStability: Float = 50f,
    val trust: Float = 50f,
    val bond: Float = 10f,
    val mentalEnergy: Float = 100f
) {
    /**
     * Precision clamping. Ensures values stay within [0, 100].
     */
    fun clamped(): PetStats {
        fun f(v: Float) = v.takeIf { it.isFinite() }?.coerceIn(0f, 100f) ?: 0f
        return copy(
            hunger = f(hunger),
            thirst = f(thirst),
            energy = f(energy),
            hygiene = f(hygiene),
            happiness = f(happiness),
            health = f(health),
            stress = f(stress),
            social = f(social),
            intelligence = f(intelligence),
            discipline = f(discipline),
            confidence = f(confidence),
            curiosity = f(curiosity),
            comfort = f(comfort),
            fitness = f(fitness),
            attention = f(attention),
            emotionalStability = f(emotionalStability),
            trust = f(trust),
            bond = f(bond),
            mentalEnergy = f(mentalEnergy)
        )
    }

    /**
     * Applies a [StatEffect] to current stats and returns the clamped result.
     */
    fun applyEffect(effect: StatEffect): PetStats {
        return copy(
            hunger = hunger + effect.hungerChange,
            thirst = thirst + effect.thirstChange,
            energy = energy + effect.energyChange,
            hygiene = hygiene + effect.hygieneChange,
            happiness = happiness + effect.happinessChange,
            health = health + effect.healthChange,
            stress = stress + effect.stressChange,
            social = social + effect.socialChange,
            intelligence = intelligence + effect.intelligenceChange,
            fitness = fitness + effect.fitnessChange,
            comfort = comfort + effect.comfortChange,
            mentalEnergy = mentalEnergy + effect.mentalEnergyChange
        ).clamped()
    }

    // Helper for UI to get display integer safely. 
    // Uses roundToInt() to ensure 99.9f becomes 100.
    fun displayHunger() = (hunger.takeIf { it.isFinite() } ?: 0f).roundToInt().coerceIn(0, 100)
    fun displayEnergy() = (energy.takeIf { it.isFinite() } ?: 0f).roundToInt().coerceIn(0, 100)
    fun displayHappiness() = (happiness.takeIf { it.isFinite() } ?: 0f).roundToInt().coerceIn(0, 100)
    fun displayHealth() = (health.takeIf { it.isFinite() } ?: 0f).roundToInt().coerceIn(0, 100)

    // Status Helpers
    fun isStarving() = hunger <= 10f
    fun isExhausted() = energy <= 10f
    fun isDepressed() = happiness <= 20f
    fun isSick() = health <= 20f
}

/**
 * The unified psychological state of the companion.
 * Represents "Continuity" rather than just a "Session".
 */
@Serializable
data class PsychologyState(
    val temperament: Temperament = Temperament.default(),
    val memories: List<MemoryAnchor> = emptyList(),
    val objectBonds: Map<String, ObjectBond> = emptyMap(),
    val currentDesires: List<Desire> = emptyList(),
    val currentActivity: ActivityType = ActivityType.IDLE,
    
    // Core Emotional Variables (0-100)
    val stress: Float = 0f,
    val burnout: Float = 0f,
    val loneliness: Float = 50f,
    val confidence: Float = 50f,
    val discipline: Float = 50f,
    val comfort: Float = 100f,
    val emotionalFatigue: Float = 0f,
    val motivation: Float = 50f,
    val addictionTendency: Float = 10f,
    val socialExhaustion: Float = 0f,
    val fulfillment: Float = 50f,
    
    val dopamineLevel: Float = 50f,
    val impulseControl: Float = 100f,
    val emotionalStability: Float = 100f
)

@Serializable
data class BuddyActivity(
    val type: ActivityType,
    val confidence: Float,
    val targetObjectId: String? = null
)

@Serializable
enum class ActivityType {
    IDLE,
    RESTING,
    LOOKING_AROUND,
    WANTING_ATTENTION,
    HIDING,
    PACING,
    PLAYING,
    GRUMPY
}

@Serializable
data class Desire(
    val type: DesireType,
    val intensity: Float, // 0.0 to 1.0
    val priority: Int = 0
)

@Serializable
enum class DesireType {
    SOLITUDE,
    STIMULATION,
    COMFORT,
    ROUTINE,
    CURIOSITY,
    AFFECTION
}

/**
 * Detailed emotional state of the pet.
 * Encapsulates primary mood, intensity, and active modifiers.
 */
@Serializable
data class EmotionState(
    val primaryMood: Mood = Mood.HAPPY,
    val intensity: Float = 0.5f, // 0.0 to 1.0
    val modifiers: List<MoodModifier> = emptyList(),
    val lastInteractionTimestamp: Long = System.currentTimeMillis()
)

/**
 * Temporary emotional shifts from specific events (e.g., feeding, petting).
 */
@Serializable
data class MoodModifier(
    val mood: Mood,
    val intensity: Float,
    val expirationTimestamp: Long,
    val source: String
)

/**
 * Enum representing the emotional state of the pet.
 * Mood is derived from stat combinations, personality, and environmental factors.
 */
@Serializable
enum class Mood {
    HAPPY,
    ANGRY,
    SAD,
    SLEEPY,
    SICK,
    BORED,
    LONELY,
    EXCITED,
    RELAXED
}

/**
 * Biased Personality Vectors.
 * These are static or slow-changing values that bias the creature's 
 * autonomous behavior and stat decay.
 */
@Serializable
data class Temperament(
    val independence: Float = 0.5f, // 0.0 (Clingy) to 1.0 (Lone Wolf)
    val curiosity: Float = 0.5f,    // 0.0 (Cautious) to 1.0 (Adventurous)
    val affection: Float = 0.5f,    // 0.0 (Aloof) to 1.0 (Warm)
    val laziness: Float = 0.5f,     // 0.0 (Energetic) to 1.0 (Sluggish)
    val resilience: Float = 0.5f    // 0.0 (Fragile) to 1.0 (Tough)
) {
    companion object {
        fun default() = Temperament()
        
        fun random() = Temperament(
            independence = (0..100).random() / 100f,
            curiosity = (0..100).random() / 100f,
            affection = (0..100).random() / 100f,
            laziness = (0..100).random() / 100f,
            resilience = (0..100).random() / 100f
        )
    }
}

@Serializable
data class BuddyAppearance(
    val hairstyle: String = "default",
    val hairColor: String = "#000000",
    val skinTone: String = "#F1C27D",
    val eyeType: String = "default",
    val eyeColor: String = "#000000",
    val eyeBrows: String = "default",
    val outfitId: String? = null
)

object AppearanceRegistry {
    val hairstyles = listOf("default", "messy", "buzz", "mohawk", "long")
    val eyeTypes = listOf("default", "sharp", "round", "cyber")
    val skinTones = listOf("#F1C27D", "#E0AC69", "#8D5524", "#C68642", "#FFDBAC")
}

/**
 * A persistent memory that anchors an emotion to an object, time, or event.
 */
@Serializable
data class MemoryAnchor(
    val id: String = UUID.randomUUID().toString(),
    val type: MemoryType,
    val sourceId: String, // e.g., Item ID or Event ID
    val intensity: Float,  // 0.0 to 1.0
    val emotionalValence: Float, // -1.0 (Traumatic) to 1.0 (Blissful)
    val timestamp: Long = System.currentTimeMillis(),
    val decayRate: Float = 0.01f // Per hour
)

@Serializable
enum class MemoryType {
    ITEM_INTERACTION,
    PLAYER_ABSENCE,
    ENVIRONMENTAL_EVENT,
    TRAUMATIC_STAT_CRASH,
    MILESTONE
}

/**
 * Tracks the "Soul" bonds with specific objects.
 */
@Serializable
data class ObjectBond(
    val itemId: String,
    val familiarity: Float = 0f,
    val attachment: Float = 0f,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
)
