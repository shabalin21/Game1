package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

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
    val casinoSession: CasinoSession = CasinoSession()
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
