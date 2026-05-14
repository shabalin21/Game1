package com.example.myapplication.domain.work.engine

import com.example.myapplication.domain.repository.EconomyRepository
import com.example.myapplication.domain.repository.PetRepository
import com.example.myapplication.domain.terminal.LogCategory
import com.example.myapplication.domain.terminal.TerminalLogManager
import com.example.myapplication.domain.work.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class JobManager @Inject constructor(
    private val petRepository: PetRepository,
    private val economyRepositoryProvider: javax.inject.Provider<EconomyRepository>,
    private val logManager: TerminalLogManager,
    private val efficiencyCalculator: EfficiencyCalculator
) {
    private val economyRepository get() = economyRepositoryProvider.get()

    private val _careerState = MutableStateFlow(PlayerCareer())
    val careerState: StateFlow<PlayerCareer> = _careerState.asStateFlow()

    private val _marketTrends = MutableStateFlow<Map<String, Float>>(emptyMap())
    val marketTrends: StateFlow<Map<String, Float>> = _marketTrends.asStateFlow()

    init {
        generateInitialMarket()
    }

    private fun generateInitialMarket() {
        val trends = JobRegistry.allJobs.associate { it.id to (0.5f + Random.nextFloat() * 2.5f) }
        _marketTrends.value = trends
    }

    fun updateMarket() {
        val currentTrends = _marketTrends.value.toMutableMap()
        JobRegistry.allJobs.forEach { job ->
            val current = currentTrends[job.id] ?: 1.0f
            // Walk randomly +- 10% but stay within [0.5, 3.0]
            val change = (Random.nextFloat() - 0.5f) * 0.2f
            currentTrends[job.id] = (current + change).coerceIn(0.5f, 3.0f)
        }
        _marketTrends.value = currentTrends
    }

    suspend fun applyForJob(job: Job): Boolean {
        val pet = petRepository.getPetState().first() ?: return false
        
        // Check requirements
        job.requirements.forEach { (stat, reqValue) ->
            val actualValue = when(stat) {
                "energy" -> pet.stats.energy
                "intelligence" -> pet.stats.intelligence
                "discipline" -> pet.stats.discipline
                "focus" -> pet.stats.attention
                "confidence" -> pet.stats.confidence
                else -> 0f
            }
            if (actualValue < reqValue) {
                logManager.log(LogCategory.WARNING, "JOB_REJECTED: Insufficient $stat (Required: $reqValue)")
                return false
            }
        }

        val currentState = _careerState.value
        if (job.type == JobType.FULL_TIME) {
            _careerState.value = currentState.copy(activeFullTimeJobId = job.id)
            logManager.log(LogCategory.SYSTEM, "New Career Path Initialized: ${job.title} at ${job.company}")
        } else {
            if (currentState.activePartTimeJobIds.size >= 2) {
                logManager.log(LogCategory.WARNING, "MAX_WORKLOAD_REACHED: Cannot accept more part-time tasks.")
                return false
            }
            _careerState.value = currentState.copy(activePartTimeJobIds = currentState.activePartTimeJobIds + job.id)
            logManager.log(LogCategory.SYSTEM, "Side-gig accepted: ${job.title}")
        }
        return true
    }

    suspend fun work(jobId: String) {
        val pet = petRepository.getPetState().first() ?: return
        val job = JobRegistry.allJobs.find { it.id == jobId } ?: return
        
        val trend = _marketTrends.value[jobId] ?: 1.0f
        val efficiency = efficiencyCalculator.calculateEfficiency(pet)
        val salary = (job.baseSalary * trend * (efficiency / 100f)).toInt()
        
        // Stats impact
        val fatigue = 15f * (1.0f + job.stressLevel)
        val stressGain = 10f * job.stressLevel
        val hungerGain = 10f
        
        val updatedStats = pet.stats.copy(
            energy = (pet.stats.energy - fatigue).coerceAtLeast(0f),
            stress = (pet.stats.stress + stressGain).coerceAtMost(100f),
            hunger = (pet.stats.hunger - hungerGain).coerceAtLeast(0f),
            discipline = (pet.stats.discipline + 0.5f).coerceAtMost(100f)
        )

        economyRepository.addCoins(salary)
        petRepository.savePetState(pet.copy(stats = updatedStats.clamped()))
        
        // Update Career State
        val currentState = _careerState.value
        val newExperience = currentState.jobExperience.toMutableMap()
        newExperience[jobId] = (newExperience[jobId] ?: 0) + 1
        
        _careerState.value = currentState.copy(
            jobExperience = newExperience,
            currentEfficiency = efficiency,
            totalEarned = currentState.totalEarned + salary,
            promotionEligibility = (newExperience[jobId] ?: 0) >= 7 && efficiency > 80f
        )

        logManager.log(LogCategory.SYSTEM, "[WORK] Shift completed. Earned: $salary CR (Efficiency: ${efficiency.toInt()}%)")
    }

    suspend fun promote() {
        val currentState = _careerState.value
        val currentJobId = currentState.activeFullTimeJobId ?: return
        if (!currentState.promotionEligibility) return

        val currentJob = JobRegistry.allJobs.find { it.id == currentJobId } ?: return
        val nextJobId = currentJob.careerPath.getOrNull(currentJob.currentLevel) ?: return
        val nextJob = JobRegistry.allJobs.find { it.id == nextJobId } ?: return

        _careerState.value = currentState.copy(
            activeFullTimeJobId = nextJob.id,
            promotionEligibility = false
        )
        logManager.log(LogCategory.SYSTEM, "PROMOTION_GRANTED: Level ${nextJob.currentLevel} - ${nextJob.title}")
    }
}
