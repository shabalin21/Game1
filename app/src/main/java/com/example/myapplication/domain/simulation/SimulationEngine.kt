package com.example.myapplication.domain.simulation

import com.example.myapplication.domain.model.*
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
    private val brainEngine: BrainEngine
) {

    fun updateState(
        pet: PetModel, 
        world: WorldState, 
        currentTimeMillis: Long,
        multipliers: Map<String, Float> = emptyMap()
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
        val expiredModifiers = pet.activeModifiers.filter { it.expirationTimestamp <= currentTimeMillis }
        val activeModifiers = pet.activeModifiers.filter { it.expirationTimestamp > currentTimeMillis }
        
        var newStats = pet.stats
        expiredModifiers.forEach { mod ->
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
            multipliers,
            pet.psychology.temperament,
            pet.employment.jobId != null,
            equipmentEffect,
            world.timeOfDay
        )
        
        // Apply active modifier accumulation (per hour)
        var modifierEffect = StatEffect()
        activeModifiers.forEach { mod ->
            modifierEffect = modifierEffect.copy(
                hungerChange = modifierEffect.hungerChange + mod.effect.hungerChange,
                energyChange = modifierEffect.energyChange + mod.effect.energyChange,
                happinessChange = modifierEffect.happinessChange + mod.effect.happinessChange,
                healthChange = modifierEffect.healthChange + mod.effect.healthChange,
                stressChange = modifierEffect.stressChange + mod.effect.stressChange,
                socialChange = modifierEffect.socialChange + mod.effect.socialChange,
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
            currentTimeMillis
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
            psychology = pet.psychology.copy(currentActivity = activity.type),
            employment = newEmployment,
            activeModifiers = activeModifiers,
            lastUpdateTimestamp = currentTimeMillis
        )
    }

    /**
     * WORK HARDER MECHANIC (Instant Impact)
     * Every press: -1 Hunger, -1 Energy, -1 Happiness, +1 Performance, +1 XP
     */
    fun applyWorkHarderEffect(pet: PetModel): PetModel {
        if (pet.employment.jobId == null || pet.isSleeping) return pet
        
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
        multipliers: Map<String, Float>,
        temperament: Temperament = Temperament.default(),
        isWorking: Boolean = false,
        equipmentEffect: StatEffect = StatEffect(),
        timeOfDay: TimeOfDay = TimeOfDay.DAY
    ): PetStats {
        val energyDecayMult = (1.0f / (multipliers["energy_decay"] ?: 1.0f)) * (1.0f + temperament.laziness * 0.5f)
        val hungerDecayMult = (1.0f / (multipliers["hunger_decay"] ?: 1.0f)) * (1.0f + temperament.curiosity * 0.2f)
        
        // Time of Day Modifiers
        val timeMultiplier = when(timeOfDay) {
            TimeOfDay.NIGHT -> 1.2f // Higher decay at night if awake
            TimeOfDay.DAWN -> 1.1f
            else -> 1.0f
        }

        val happinessDecayMult = (1.0f / (multipliers["happiness_decay"] ?: 1.0f)) * (1.5f - temperament.affection * 0.5f) * timeMultiplier
        
        val workMultiplier = if (isWorking) 1.5f else 1.0f

        // BELIEVABLE INFLUENCES (THINGS THAT DECREASE HAPPINESS)
        val hungerPenalty = if (stats.hunger < SC.CRITICAL_NEED_THRESHOLD) (SC.CRITICAL_NEED_THRESHOLD - stats.hunger) / 5f else 0f
        val energyPenalty = if (stats.energy < SC.CRITICAL_NEED_THRESHOLD) (SC.CRITICAL_NEED_THRESHOLD - stats.energy) / 5f else 0f
        val stressPenalty = stats.stress / 25f
        val lonelinessPenalty = if (stats.social < SC.CRITICAL_NEED_THRESHOLD) (SC.CRITICAL_NEED_THRESHOLD - stats.social) / 10f else 0f
        
        val totalHappinessPenalty = hungerPenalty + energyPenalty + stressPenalty + lonelinessPenalty
        val happinessModifier = 1.0f + (totalHappinessPenalty * SC.HAPPINESS_PENALTY_MULTIPLIER)

        return if (isSleeping) {
            val energyRegenMult = (multipliers["energy_regen"] ?: 1.0f) * (1.5f - temperament.laziness * 0.5f)
            // Homes improve sleep recovery
            val homeSleepBonus = (equipmentEffect.comfortChange.coerceAtLeast(0f) * 0.2f)
            
            stats.copy(
                energy = stats.energy + ((SC.SLEEP_ENERGY_RECOVERY + homeSleepBonus) * energyRegenMult * hours),
                hunger = stats.hunger - (SC.SLEEP_HUNGER_DECAY * hours),
                happiness = stats.happiness - (SC.SLEEP_HAPPINESS_DECAY * hours)
            )
        } else {
            val exhaustionFactor = if (stats.hunger <= SC.MIN_STAT_VALUE) 2.0f else 1.0f
            
            // Equipment provides passive happiness stability
            val passiveHappinessBonus = equipmentEffect.happinessChange * hours

            stats.copy(
                energy = stats.energy - (SC.AWAKE_ENERGY_DECAY * energyDecayMult * hours * exhaustionFactor * workMultiplier),
                hunger = stats.hunger - (SC.AWAKE_HUNGER_DECAY * hungerDecayMult * hours * workMultiplier),
                happiness = stats.happiness - (SC.AWAKE_HAPPINESS_DECAY * happinessDecayMult * hours * workMultiplier * happinessModifier) + passiveHappinessBonus,
                stress = stats.stress + (equipmentEffect.stressChange * hours) // Passive stress reduction from home
            )
        }
    }
}
