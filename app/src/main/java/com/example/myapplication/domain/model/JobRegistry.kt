package com.example.myapplication.domain.model

object JobRegistry {
    val allJobs = listOf(
        // ENTRY TIER
        JobModel("lab_assistant", "Lab Assistant", "🔬", 25, 1.1f, 1.2f, 1, JobTier.ENTRY, 0, 1, "Basic research and facility maintenance."),
        JobModel("data_entry", "Data Entry", "🧹", 20, 1.0f, 1.1f, 1, JobTier.ENTRY, 0, 1, "Clerical work and data organization."),
        JobModel("night_security", "Night Security", "🔦", 30, 0.8f, 1.8f, 2, JobTier.ENTRY, 100, 2, "Monitoring facility surveillance."),
        
        // SPECIALIST TIER
        JobModel("developer", "Junior Developer", "💻", 45, 1.3f, 1.5f, 3, JobTier.SPECIALIST, 250, 2, "Writing and debugging software scripts."),
        JobModel("logistics_op", "Logistics Operator", "🎮", 65, 1.5f, 1.6f, 5, JobTier.SPECIALIST, 500, 3, "Coordinating remote supply chains."),
        JobModel("courier", "Express Courier", "🏃", 55, 1.4f, 2.0f, 4, JobTier.SPECIALIST, 400, 3, "Physical delivery of high-priority packages."),
        
        // EXPERT TIER
        JobModel("analyst", "Data Analyst", "🧠", 90, 1.8f, 1.5f, 8, JobTier.EXPERT, 1000, 4, "Interpreting complex data patterns."),
        JobModel("security_analyst", "Security Analyst", "🛡️", 110, 2.0f, 1.4f, 15, JobTier.EXPERT, 1500, 4, "Protecting corporate networks."),
        JobModel("technician", "Maintenance Tech", "☢️", 150, 2.5f, 3.0f, 10, JobTier.EXPERT, 2000, 5, "Critical maintenance in high-risk zones."),
        
        // ELITE TIER
        JobModel("consultant", "Strategic Consultant", "🏴‍☠️", 250, 3.5f, 2.5f, 20, JobTier.ELITE, 5000, 5, "High-stakes corporate troubleshooting."),
        JobModel("architect", "Systems Architect", "🏗️", 200, 1.5f, 1.2f, 25, JobTier.ELITE, 4000, 3, "Designing large-scale infrastructure."),
        
        // EXECUTIVE TIER
        JobModel("ceo", "Corporate CEO", "🏢", 1200, 5.0f, 1.0f, 50, JobTier.EXECUTIVE, 50000, 1, "Leading a multi-sector mega-corporation."),
        JobModel("investor", "Shadow Investor", "🏦", 850, 4.0f, 1.2f, 40, JobTier.EXECUTIVE, 25000, 2, "Controlling city-wide financial flows.")
    )

    fun getJob(id: String): JobModel? = allJobs.find { it.id == id }
}
