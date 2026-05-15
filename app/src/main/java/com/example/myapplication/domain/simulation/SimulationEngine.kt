package com.example.myapplication.domain.simulation

import com.example.myapplication.core.modifier.Modifier
import com.example.myapplication.core.modifier.ModifierPipeline
import com.example.myapplication.core.modifier.ModifierRegistry
import com.example.myapplication.core.modifier.ModifierType
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.simulation.emotion.EmotionalEngine
import com.example.myapplication.domain.simulation.SimulationConstants as SC
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * PRODUCTION-GRADE SIMULATION ENGINE.
 * Final balance pass for SSS-tier feel.
 */
@Singleton
class SimulationEngine @Inject constructor(
    private val moodEngine: MoodEngine,
    private val brainEngine: BrainEngine,
    private val emotionalEngine: EmotionalEngine,
) {

    fun updateState(
        pet: PetModel, 
        world: WorldState, 
        currentTimeMillis: Long,
        externalModifiers: List<Modifier> = emptyList()
    ): PetModel {
        val lastUpdate = pet.lastUpdateTimestamp
        val elapsedMillis = currentTimeMillis - lastUpdate
        
        // Ensure strictly monotonic or at least non-negative time
        if (elapsedMillis <= 0) return pet

        // Clamp simulation jump to prevent massive stat drains (max 24h)
        val maxJumpMillis = TimeUnit.HOURS.toMillis(SC.MAX_SIMULATION_JUMP_HOURS)
        val sanitizedElapsedMillis = elapsedMillis.coerceAtMost(maxJumpMillis)
        val elapsedHours = sanitizedElapsedMillis.toFloat() / TimeUnit.HOURS.toMillis(1)
        
        // 1. PROCESS ACTIVE MODIFIERS (Remove expired & apply side effects)
        val expiredTimed = pet.activeModifiers.filter { it.expirationTimestamp <= currentTimeMillis }
        val activeTimed = pet.activeModifiers.filter { it.expirationTimestamp > currentTimeMillis }
        
        // New Systemic Modifiers
        val activeModifiers = pet.modifiers.filter { !it.isExpired }
        
        // 1.2 EMOTIONAL SIMULATION
        val nextPsychology = emotionalEngine.tick(pet.psychology, pet.stats, world, elapsedHours)
        val emotionalModifiers = emotionalEngine.resolveModifiers(nextPsychology)
        
        val totalModifiers = activeModifiers + externalModifiers + emotionalModifiers
        
        var newStats = pet.stats
        expiredTimed.forEach { mod ->
            mod.sideEffect?.let { side ->
                newStats = newStats.applyEffect(side)
                Timber.i("Simulation: Buff expired, applying side effect for ${mod.name}")
            }
        }

        // 1.5 EQUIPMENT EFFECTS (Clothes & Homes)
        val equipmentEffect = calculateEquipmentEffects(pet.equippedItems)
        
        // 2. STAT DECAY
        newStats = calculateSimulationTick(
            newStats,
            elapsedHours, 
            pet.isSleeping, 
            totalModifiers,
            pet.psychology.temperament,
            pet.employment.jobId != null,
            equipmentEffect,
            world
        )
        
        // Apply active modifier accumulation (per hour) - Legacy System
        var modifierEffect = StatEffect()
        activeTimed.forEach { mod ->
            modifierEffect = modifierEffect.copy(
                hungerChange = modifierEffect.hungerChange + mod.effect.hungerChange,
                energyChange = modifierEffect.energyChange + mod.effect.energyChange,
                happinessChange = modifierEffect.happinessChange + mod.effect.happinessChange,
                healthChange = modifierEffect.healthChange + mod.effect.healthChange,
                stressChange = modifierEffect.stressChange + mod.effect.stressChange,
                socialChange = modifierEffect.socialChange + mod.effect.socialChange,
                hygieneChange = modifierEffect.hygieneChange + mod.effect.hygieneChange,
                intelligenceChange = modifierEffect.intelligenceChange + mod.effect.intelligenceChange,
                fitnessChange = modifierEffect.fitnessChange + mod.effect.fitnessChange,
                comfortChange = modifierEffect.comfortChange + mod.effect.comfortChange
            )
        }

        newStats = newStats.copy(
            hunger = newStats.hunger + (modifierEffect.hungerChange * elapsedHours),
            energy = newStats.energy + (modifierEffect.energyChange * elapsedHours),
            happiness = newStats.happiness + (modifierEffect.happinessChange * elapsedHours),
            health = newStats.health + (modifierEffect.healthChange * elapsedHours),
            stress = newStats.stress + (modifierEffect.stressChange * elapsedHours),
            social = newStats.social + (modifierEffect.socialChange * elapsedHours),
            hygiene = newStats.hygiene + (modifierEffect.hygieneChange * elapsedHours),
            intelligence = newStats.intelligence + (modifierEffect.intelligenceChange * elapsedHours),
            fitness = newStats.fitness + (modifierEffect.fitnessChange * elapsedHours),
            comfort = newStats.comfort + (modifierEffect.comfortChange * elapsedHours)
        ).clamped()

        var isSleeping = pet.isSleeping


        // 2. JOB PERFORMANCE DECAY & SALARY
        val (newEmployment, _) = calculateJobTick(
            pet.employment,
            newStats,
            elapsedHours,
            currentTimeMillis
        )

        // AUTO-WAKE AT 100%
        if (isSleeping && (newStats.energy >= SC.MAX_STAT_VALUE)) {
            Timber.i("Simulation: Companion fully rested. Waking up.")
            isSleeping = false
            newStats = newStats.copy(
                energy = SC.MAX_STAT_VALUE,
                happiness = (newStats.happiness + 10f).coerceAtMost(SC.MAX_STAT_VALUE)
            )
        } 
        
        val newEmotion = moodEngine.updateEmotionState(
            pet.emotionState,
            newStats,
            pet.psychology,
            currentTimeMillis,
            world,
            pet.memoryGraph
        )
        
        // Calculate current in-game hour (1 minute = 1 hour, 24 minutes = 1 day)
        val minutesInDay = 24
        val currentMinuteOfDay = ((currentTimeMillis / 1000 / 60) % minutesInDay).toInt()
        val hour = currentMinuteOfDay // 1:1 mapping in this simulation
        
        // AUTO-EVALUATE BUDDY ACTIVITY
        val activity = brainEngine.evaluateActivity(pet.copy(stats = newStats, emotionState = newEmotion), world, hour)

        // 3. CALCULATE LIFESTYLE POINTS (Unified Progression)
        val wealthFactor = (pet.employment.totalEarned / 1000f).coerceAtMost(50f)
        val happinessFactor = (newStats.happiness / 10f).coerceAtMost(10f)
        val socialFactor = (pet.social.followers / 500f).coerceAtMost(40f)
        
        val pointsEarned = ((wealthFactor + happinessFactor + socialFactor) * elapsedHours).toLong()

        return pet.copy(
            stats = newStats.clamped(),
            emotionState = newEmotion,
            lifestylePoints = pet.lifestylePoints + pointsEarned,
            isSleeping = isSleeping,
            psychology = nextPsychology.copy(currentActivity = activity.type),
            employment = newEmployment,
            activeModifiers = activeTimed,
            modifiers = activeModifiers,
            lastUpdateTimestamp = currentTimeMillis
        )
    }

    /**
     * WORK HARDER MECHANIC (Instant Impact)
     * Every press: -1 Hunger, -1 Energy, -1 Happiness, +1 Performance, +1 XP
     */
    fun applyWorkHarderEffect(pet: PetModel): PetModel {
        if ((pet.employment.jobId == null) || pet.isSleeping) return pet
        
        val updatedStats = pet.stats.copy(
            hunger = pet.stats.hunger - 1f,
            energy = pet.stats.energy - 1f,
            happiness = pet.stats.happiness - 1f
        ).clamped()
        
        val updatedEmployment = pet.employment.copy(
            performance = (pet.employment.performance + 1.0f).coerceAtMost(SC.MAX_STAT_VALUE),
            experience = pet.employment.experience + 1
        )
        
        return pet.copy(
            stats = updatedStats,
            employment = updatedEmployment,
            xp = pet.xp + 1 // Ensure XP increment as well
        )
    }

    fun applyItemEffect(stats: PetStats, item: ItemModel): PetStats {
        return stats.applyEffect(item.effect)
    }

    fun getMinigameCost(): StatEffect {
        return StatEffect(
            energyChange = -10f,
            hungerChange = -5f,
            happinessChange = 5f
        )
    }



    private fun calculateJobTick(
        state: EmploymentState,
        stats: PetStats,
        hours: Float,
        currentTime: Long
    ): Pair<EmploymentState, Int> {
        val jobId = state.jobId ?: return state to 0
        val job = JobRegistry.getJob(jobId) ?: return state.copy(jobId = null) to 0

        // Performance Decay
        val baseDecay = 5.0f * job.difficulty // 5% base per hour
        val statPenalty = (SC.MAX_STAT_VALUE - stats.energy) / 50f + (SC.MAX_STAT_VALUE - stats.happiness) / 50f
        val performanceDecay = baseDecay + statPenalty
        
        val newPerformance = (state.performance - performanceDecay * hours).coerceIn(SC.MIN_STAT_VALUE, SC.MAX_STAT_VALUE)
        
        // Salary Calculation
        val payMult = when {
            newPerformance >= 80f -> 1.0f
            newPerformance >= 60f -> 0.7f
            newPerformance >= 40f -> 0.4f
            else -> 0.1f
        }
        val earned = (job.hourlyPay * payMult * hours).toInt()
        
        // Experience gain (10 XP per hour worked if performance > 40%)
        val xpGain = if (newPerformance > 40f) (10f * hours).toInt() else 0

        // Firing Logic
        val isFired = newPerformance < 5f // Threshold
        if (isFired) {
            return state.copy(
                jobId = null,
                performance = 0f,
                firedCooldownTimestamp = currentTime + TimeUnit.HOURS.toMillis(1)
            ) to earned
        }

        return state.copy(
            performance = newPerformance,
            experience = state.experience + xpGain,
            totalEarned = state.totalEarned + earned,
            isWarningsIssued = newPerformance < 30f
        ) to earned
    }

    private fun calculateEquipmentEffects(equipped: Map<ItemCategory, String>): StatEffect {
        var totalEffect = StatEffect()
        
        // 1. CLOTHING BONUSES
        val clothesId = equipped[ItemCategory.CLOTHES]
        if (clothesId != null) {
            ItemRegistry.getItem(clothesId)?.let { item ->
                // Apply a portion of the stat effect as a passive bonus per hour
                totalEffect = totalEffect.copy(
                    happinessChange = totalEffect.happinessChange + (item.effect.happinessChange * 0.1f),
                    comfortChange = totalEffect.comfortChange + (item.effect.comfortChange * 0.2f),
                    socialChange = totalEffect.socialChange + (item.effect.socialChange * 0.15f)
                )
            }
        } else {
            // "Naked" penalty
            totalEffect = totalEffect.copy(happinessChange = -2.0f, comfortChange = -5.0f)
        }

        // 2. HOME BONUSES
        val homeId = equipped[ItemCategory.HOMES]
        if (homeId != null) {
            ItemRegistry.getItem(homeId)?.let { item ->
                totalEffect = totalEffect.copy(
                    happinessChange = totalEffect.happinessChange + (item.effect.happinessChange * 0.05f),
                    comfortChange = totalEffect.comfortChange + (item.effect.comfortChange * 0.1f),
                    stressChange = totalEffect.stressChange + (item.effect.stressChange * 0.1f)
                )
            }
        }

        // 3. VEHICLE BONUSES
        val vehicleId = equipped[ItemCategory.VEHICLES]
        if (vehicleId != null) {
            ItemRegistry.getItem(vehicleId)?.let { item ->
                totalEffect = totalEffect.copy(
                    energyChange = totalEffect.energyChange + (item.effect.energyChange * 0.05f),
                    happinessChange = totalEffect.happinessChange + (item.effect.happinessChange * 0.05f)
                )
            }
        }

        // 4. JEWELRY BONUSES
        val jewelryId = equipped[ItemCategory.JEWELRY]
        if (jewelryId != null) {
            ItemRegistry.getItem(jewelryId)?.let { item ->
                totalEffect = totalEffect.copy(
                    socialChange = totalEffect.socialChange + (item.effect.socialChange * 0.1f),
                    happinessChange = totalEffect.happinessChange + (item.effect.happinessChange * 0.02f)
                )
            }
        }

        // 5. PET BONUSES
        val petId = equipped[ItemCategory.PETS]
        if (petId != null) {
            ItemRegistry.getItem(petId)?.let { item ->
                totalEffect = totalEffect.copy(
                    happinessChange = totalEffect.happinessChange + (item.effect.happinessChange * 0.1f),
                    socialChange = totalEffect.socialChange + (item.effect.socialChange * 0.1f),
                    stressChange = totalEffect.stressChange + (item.effect.stressChange * 0.1f)
                )
            }
        }
        
        return totalEffect
    }

    private fun calculateSimulationTick(
        stats: PetStats,
        hours: Float,
        isSleeping: Boolean,
        modifiers: List<Modifier>,
        temperament: Temperament = Temperament.default(),
        isWorking: Boolean = false,
        equipmentEffect: StatEffect = StatEffect(),
        worldState: WorldState
    ): PetStats {
        // ENERGY DECAY
        val energyDecayBase = if (isSleeping) 0f else SC.AWAKE_ENERGY_DECAY
        val energyDecayRate = ModifierPipeline.calculate(
            energyDecayBase,
            modifiers.filter { it.tag == ModifierRegistry.DECAY_ENERGY }
        ) * (1.0f + temperament.laziness * 0.5f)

        // HUNGER DECAY
        val hungerDecayBase = if (isSleeping) SC.SLEEP_HUNGER_DECAY else SC.AWAKE_HUNGER_DECAY
        val hungerDecayRate = ModifierPipeline.calculate(
            hungerDecayBase,
            modifiers.filter { it.tag == ModifierRegistry.DECAY_HUNGER }
        ) * (1.0f + temperament.curiosity * 0.2f)

        // HAPPINESS DECAY
        val happinessDecayBase = if (isSleeping) SC.SLEEP_HAPPINESS_DECAY else SC.AWAKE_HAPPINESS_DECAY
        val happinessDecayRate = ModifierPipeline.calculate(
            happinessDecayBase,
            modifiers.filter { it.tag == ModifierRegistry.DECAY_HAPPINESS }
        ) * (1.5f - temperament.affection * 0.5f)

        val workMultiplier = if (isWorking) 1.5f else 1.0f
        
        // WEATHER EFFECTS
        val weatherMultiplier = when(worldState.weather) {
            Weather.RAINY -> 1.1f
            Weather.STORMY -> 1.3f
            else -> 1.0f
        }
        
        val weatherStress = when(worldState.weather) {
            Weather.STORMY -> 5.0f * hours
            Weather.RAINY -> 1.0f * hours
            else -> 0f
        }

        // BELIEVABLE INFLUENCES (THINGS THAT DECREASE HAPPINESS)
        val hungerPenalty = if (stats.hunger < SC.CRITICAL_NEED_THRESHOLD) (SC.CRITICAL_NEED_THRESHOLD - stats.hunger) / 5f else 0f
        val energyPenalty = if (stats.energy < SC.CRITICAL_NEED_THRESHOLD) (SC.CRITICAL_NEED_THRESHOLD - stats.energy) / 5f else 0f
        val hygienePenalty = if (stats.hygiene < SC.CRITICAL_NEED_THRESHOLD) (SC.CRITICAL_NEED_THRESHOLD - stats.hygiene) / 10f else 0f
        val stressPenalty = stats.stress / 25f
        val lonelinessPenalty = if (stats.social < SC.CRITICAL_NEED_THRESHOLD) (SC.CRITICAL_NEED_THRESHOLD - stats.social) / 10f else 0f
        
        val totalHappinessPenalty = hungerPenalty + energyPenalty + hygienePenalty + stressPenalty + lonelinessPenalty
        val happinessModifier = 1.0f + (totalHappinessPenalty * SC.HAPPINESS_PENALTY_MULTIPLIER)

        return if (isSleeping) {
            val energyRegenBase = SC.SLEEP_ENERGY_RECOVERY
            val energyRegenRate = ModifierPipeline.calculate(
                energyRegenBase,
                modifiers.filter { it.tag == ModifierRegistry.REGEN_ENERGY }
            ) * (1.5f - temperament.laziness * 0.5f)

            // Homes improve sleep recovery
            val homeSleepBonus = (equipmentEffect.comfortChange.coerceAtLeast(0f) * 0.2f)
            
            stats.copy(
                energy = stats.energy + ((energyRegenRate + homeSleepBonus) * hours),
                hunger = stats.hunger - (hungerDecayRate * hours),
                happiness = stats.happiness - (happinessDecayRate * hours),
                hygiene = stats.hygiene - (SC.SLEEP_HYGIENE_DECAY * hours)
            )
        } else {
            val exhaustionFactor = if (stats.hunger <= SC.MIN_STAT_VALUE) 2.0f else 1.0f
            
            // Equipment provides passive happiness stability
            val passiveHappinessBonus = equipmentEffect.happinessChange * hours
            
            // Passive Trust/Bond Growth
            val trustGrowth = if (stats.happiness > 70f) SC.TRUST_GROWTH_BASE * hours else 0f
            val bondGrowth = if (stats.happiness > 80f && stats.hunger > 50f) SC.BOND_GROWTH_BASE * hours else 0f

            stats.copy(
                energy = stats.energy - (energyDecayRate * hours * exhaustionFactor * workMultiplier * weatherMultiplier),
                hunger = stats.hunger - (hungerDecayRate * hours * workMultiplier),
                happiness = stats.happiness - (happinessDecayRate * hours * workMultiplier * happinessModifier) + passiveHappinessBonus,
                hygiene = stats.hygiene - (SC.AWAKE_HYGIENE_DECAY * hours),
                social = stats.social - (SC.AWAKE_SOCIAL_DECAY * hours),
                stress = stats.stress + (equipmentEffect.stressChange * hours) + weatherStress,
                trust = stats.trust + trustGrowth,
                bond = stats.bond + bondGrowth
            )
        }
    }
}
