package com.example.myapplication.domain.work.model

import kotlinx.serialization.Serializable

enum class JobType {
    FULL_TIME, PART_TIME
}

@Serializable
data class Job(
    val id: String,
    val title: String,
    val company: String,
    val type: JobType,
    val baseSalary: Int,
    val stressLevel: Float, // 0.0 to 1.0
    val requirements: Map<String, Float>,
    val description: String,
    val careerPath: List<String> = emptyList(), // List of Job IDs
    val currentLevel: Int = 1
)

@Serializable
data class PlayerCareer(
    val activeFullTimeJobId: String? = null,
    val activePartTimeJobIds: List<String> = emptyList(),
    val jobExperience: Map<String, Int> = emptyMap(), // jobId to days worked
    val currentEfficiency: Float = 100f,
    val totalEarned: Long = 0,
    val promotionEligibility: Boolean = false
)

data class WorkResult(
    val earned: Int,
    val efficiency: Float,
    val statChanges: Map<String, Float>,
    val logs: List<String>
)

object JobRegistry {
    val allJobs = listOf(
        // Full-Time: Courier Path
        Job(
            id = "courier_1",
            title = "Junior Courier",
            company = "VELOCITY_EXPRESS",
            type = JobType.FULL_TIME,
            baseSalary = 150,
            stressLevel = 0.3f,
            requirements = mapOf("energy" to 40f),
            description = "Entry-level delivery tasking in the lower sectors.",
            careerPath = listOf("courier_1", "courier_2", "courier_3")
        ),
        Job(
            id = "courier_2",
            title = "Senior Courier",
            company = "VELOCITY_EXPRESS",
            type = JobType.FULL_TIME,
            baseSalary = 250,
            stressLevel = 0.5f,
            requirements = mapOf("energy" to 60f, "discipline" to 40f),
            description = "High-priority cargo handling with tighter deadlines.",
            currentLevel = 2
        ),
        
        // Full-Time: Corporate Path
        Job(
            id = "corp_1",
            title = "Junior Analyst",
            company = "AETHER_CORP",
            type = JobType.FULL_TIME,
            baseSalary = 200,
            stressLevel = 0.4f,
            requirements = mapOf("intelligence" to 50f, "focus" to 60f),
            description = "Data processing and neural pattern recognition.",
            careerPath = listOf("corp_1", "corp_2", "corp_3")
        ),

        // Part-Time
        Job(
            id = "freelance_code",
            title = "Freelance Coding",
            company = "GIG_NET",
            type = JobType.PART_TIME,
            baseSalary = 80,
            stressLevel = 0.2f,
            requirements = mapOf("intelligence" to 40f),
            description = "Short-term scripting and patch jobs."
        ),
        Job(
            id = "night_security",
            title = "Night Security",
            company = "IRON_SIGHT",
            type = JobType.PART_TIME,
            baseSalary = 100,
            stressLevel = 0.4f,
            requirements = mapOf("confidence" to 50f, "health" to 50f),
            description = "Surveillance of decommissioned factory zones."
        )
    )
}
