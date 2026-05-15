package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemCategory {
    PRODUCTS, CLOTHES, ITEMS, HOMES, VEHICLES, JEWELRY, PETS,
    // Cosmetic Granularity
    HAIR, EYES, SKIN, AURA, ACCESSORY_HEAD, ACCESSORY_BODY,
    // Legacy mapping
    FOOD, DRINKS, MEDICAL, ELECTRONICS, GYM, GAMBLING, LUXURY, MISC,
    HEAD, TOP, BOTTOM, SHOES, ACCESSORY, FURNITURE, BOOSTER, RARE, ILLEGAL, DECORATION, WORK_ENHANCER, MEDICINE, TOY, COSMETIC
}

@Serializable
enum class ItemRarity {
    COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, GOLD
}

@Serializable
enum class BuffType {
    NONE, CAFFEINE, SUGAR_RUSH, FOCUS, RELAXATION, MEDICINE, ADRENALINE, ADDICTION
}

@Serializable
data class ItemModel(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val price: Int,
    val category: ItemCategory,
    val rarity: ItemRarity = ItemRarity.COMMON,
    val effect: StatEffect = StatEffect(),
    val durationMillis: Long = 0, // 0 for instant effects
    val buffType: BuffType = BuffType.NONE,
    val sideEffect: StatEffect? = null,
    val unlockLevel: Int = 1,
    val isConsumable: Boolean = true,
    val floatValue: Float? = null // For GOLD items: 0.9 to 1.0
)

@Serializable
data class StatEffect(
    val hungerChange: Float = 0f,
    val energyChange: Float = 0f,
    val happinessChange: Float = 0f,
    val healthChange: Float = 0f,
    val stressChange: Float = 0f,
    val socialChange: Float = 0f,
    val hygieneChange: Float = 0f,
    val intelligenceChange: Float = 0f,
    val fitnessChange: Float = 0f,
    val comfortChange: Float = 0f
)

/**
 * ITEM REGISTRY.
 * Categorized into PRODUCTS, CLOTHES, ITEMS, HOMES.
 */
object ItemRegistry {
    val allItems = listOf(
        // --- 1. PRODUCTS (Consumables) ---
        ItemModel("coffee", "Midnight Espresso", "☕", "A smooth, dark roast for late nights.", 45, ItemCategory.PRODUCTS, ItemRarity.COMMON, 
            effect = StatEffect(energyChange = 25f, stressChange = -5f),
            durationMillis = 1000 * 60 * 5, buffType = BuffType.CAFFEINE,
            sideEffect = StatEffect(energyChange = -15f, stressChange = 5f)
        ),
        ItemModel("burger", "Street Burger", "🍔", "Classic diner-style burger.", 65, ItemCategory.PRODUCTS, ItemRarity.COMMON, 
            effect = StatEffect(hungerChange = 45f, happinessChange = 10f, energyChange = -5f)
        ),
        ItemModel("pizza", "Late Night Pizza", "🍕", "The perfect comfort food.", 85, ItemCategory.PRODUCTS, ItemRarity.UNCOMMON, 
            effect = StatEffect(hungerChange = 60f, happinessChange = 15f, healthChange = -2f)
        ),
        ItemModel("soda", "Neon Fizz", "🥤", "Sparkling soda with a hint of citrus.", 20, ItemCategory.PRODUCTS, ItemRarity.COMMON, 
            effect = StatEffect(energyChange = 10f, happinessChange = 5f, healthChange = -1f),
            buffType = BuffType.SUGAR_RUSH, durationMillis = 1000 * 60 * 2
        ),
        ItemModel("snacks", "Crunchy Bites", "🥨", "Salty, crunchy, and addictive.", 15, ItemCategory.PRODUCTS, ItemRarity.COMMON, 
            effect = StatEffect(hungerChange = 10f, happinessChange = 3f)
        ),
        ItemModel("cigarettes", "Herbal Sticks", "🚬", "Helps calm the nerves.", 50, ItemCategory.PRODUCTS, ItemRarity.UNCOMMON, 
            effect = StatEffect(stressChange = -20f, happinessChange = 5f),
            buffType = BuffType.ADDICTION, sideEffect = StatEffect(healthChange = -5f, stressChange = 10f)
        ),
        ItemModel("medicine", "Recovery Kit", "💊", "Advanced medical supplies.", 250, ItemCategory.PRODUCTS, ItemRarity.RARE, 
            effect = StatEffect(healthChange = 50f, stressChange = -10f),
            buffType = BuffType.MEDICINE, durationMillis = 1000 * 60 * 10
        ),
        ItemModel("energy_drink", "Surge Energy", "⚡", "Instant focus and energy.", 110, ItemCategory.PRODUCTS, ItemRarity.RARE, 
            effect = StatEffect(energyChange = 50f, hungerChange = -10f),
            buffType = BuffType.ADRENALINE, durationMillis = 1000 * 60 * 8,
            sideEffect = StatEffect(energyChange = -30f, stressChange = 15f)
        ),

        // --- 2. CLOTHES (Wearables) ---
        ItemModel("hoodie_black", "Street Hoodie", "🧥", "Simple, warm, and comfortable.", 450, ItemCategory.CLOTHES, ItemRarity.COMMON, 
            effect = StatEffect(happinessChange = 5f, comfortChange = 10f), isConsumable = false
        ),
        ItemModel("jacket_leather", "Classic Leather Jacket", "🧥", "A timeless style statement.", 1200, ItemCategory.CLOTHES, ItemRarity.RARE, 
            effect = StatEffect(happinessChange = 15f, socialChange = 25f), isConsumable = false
        ),
        ItemModel("sunglasses", "Reflective Shades", "🕶️", "Look cool, even indoors.", 350, ItemCategory.CLOTHES, ItemRarity.UNCOMMON, 
            effect = StatEffect(happinessChange = 5f, socialChange = 10f), isConsumable = false
        ),
        ItemModel("sneakers_neon", "Neon Runners", "👟", "Stylish shoes for long walks.", 800, ItemCategory.CLOTHES, ItemRarity.EPIC, 
            effect = StatEffect(happinessChange = 10f, fitnessChange = 15f), isConsumable = false
        ),
        ItemModel("hat_beanie", "Soft Beanie", "🧶", "Keeping warm and stylish.", 200, ItemCategory.CLOTHES, ItemRarity.COMMON, 
            effect = StatEffect(happinessChange = 2f, comfortChange = 5f), isConsumable = false
        ),
        ItemModel("watch_gold", "Prestige Gold Watch", "⌚", "A symbol of success.", 25000, ItemCategory.CLOTHES, ItemRarity.GOLD, 
            effect = StatEffect(happinessChange = 30f, socialChange = 50f), isConsumable = false, floatValue = 0.98f
        ),

        // --- 3. ITEMS (Utility) ---
        ItemModel("phone", "Smart Phone", "📱", "Stay connected with everyone.", 900, ItemCategory.ITEMS, ItemRarity.RARE, 
            effect = StatEffect(happinessChange = 10f, socialChange = 20f), isConsumable = false
        ),
        ItemModel("laptop", "Workstation Pro", "💻", "Professional tools for serious work.", 2500, ItemCategory.ITEMS, ItemRarity.EPIC, 
            effect = StatEffect(intelligenceChange = 25f), isConsumable = false
        ),
        ItemModel("headphones", "Studio Headphones", "🎧", "Immersive sound, zero distractions.", 600, ItemCategory.ITEMS, ItemRarity.UNCOMMON, 
            effect = StatEffect(happinessChange = 10f, stressChange = -15f), isConsumable = false
        ),
        ItemModel("gym_pass", "Fitness Pass", "🎫", "Permanent access to local fitness centers.", 1000, ItemCategory.ITEMS, ItemRarity.RARE, 
            isConsumable = false
        ),
        ItemModel("lottery_ticket", "City Lotto", "🎫", "A chance to change your life.", 100, ItemCategory.ITEMS, ItemRarity.RARE, 
            isConsumable = true
        ),
        ItemModel("repair_kit", "First Aid Kit", "🛠️", "Everything you need for minor fixes.", 150, ItemCategory.ITEMS, ItemRarity.UNCOMMON, 
            effect = StatEffect(healthChange = 10f, comfortChange = 5f)
        ),

        // --- 4. HOMES (Progression) ---
        ItemModel("home_tiny", "Capsule Pod", "📦", "Minimum space, maximum efficiency.", 2500, ItemCategory.HOMES, ItemRarity.COMMON, 
            effect = StatEffect(comfortChange = 5f, happinessChange = 5f), isConsumable = false
        ),
        ItemModel("home_studio", "Neon Apartment", "🏙️", "A stylish place with a view.", 12000, ItemCategory.HOMES, ItemRarity.UNCOMMON, 
            effect = StatEffect(comfortChange = 15f, happinessChange = 15f, stressChange = -10f), isConsumable = false
        ),
        ItemModel("home_modern", "High-Rise Loft", "🌃", "Spacious living for the rising elite.", 45000, ItemCategory.HOMES, ItemRarity.RARE, 
            effect = StatEffect(comfortChange = 30f, happinessChange = 25f, stressChange = -20f), isConsumable = false
        ),
        ItemModel("home_luxury", "Penthouse Suite", "💎", "The ultimate city life experience.", 180000, ItemCategory.HOMES, ItemRarity.LEGENDARY,
            effect = StatEffect(comfortChange = 60f, happinessChange = 50f, stressChange = -40f), isConsumable = false
        ),
        ItemModel("home_villa", "Cyber Villa", "🏰", "Unmatched luxury and security.", 1200000, ItemCategory.HOMES, ItemRarity.GOLD,
            effect = StatEffect(comfortChange = 100f, happinessChange = 100f, stressChange = -80f, socialChange = 50f), isConsumable = false
        ),

        // --- 5. VEHICLES (Efficiency) ---
        ItemModel("vec_bicycle", "Street Bike", "🚲", "Eco-friendly and slow.", 400, ItemCategory.VEHICLES, ItemRarity.COMMON,
            effect = StatEffect(fitnessChange = 5f, happinessChange = 5f), isConsumable = false
        ),
        ItemModel("vec_scooter", "Volt Scooter", "🛴", "Zip through traffic.", 1500, ItemCategory.VEHICLES, ItemRarity.UNCOMMON,
            effect = StatEffect(energyChange = 10f, socialChange = 5f), isConsumable = false
        ),
        ItemModel("vec_car_cheap", "Rusty Sedan", "🚗", "It gets you there eventually.", 8000, ItemCategory.VEHICLES, ItemRarity.UNCOMMON,
            effect = StatEffect(energyChange = 20f, comfortChange = 10f), isConsumable = false
        ),
        ItemModel("vec_car_sports", "Apex GT", "🏎️", "Pure speed and adrenaline.", 65000, ItemCategory.VEHICLES, ItemRarity.EPIC,
            effect = StatEffect(happinessChange = 40f, socialChange = 30f, energyChange = 30f), isConsumable = false
        ),
        ItemModel("vec_hover", "Hover Drifter", "🛸", "Gravity is optional.", 450000, ItemCategory.VEHICLES, ItemRarity.GOLD,
            effect = StatEffect(happinessChange = 80f, socialChange = 100f, energyChange = 50f), isConsumable = false
        ),

        // --- 6. JEWELRY (Status) ---
        ItemModel("jwl_silver_chain", "Silver Chain", "⛓️", "A simple statement.", 3500, ItemCategory.JEWELRY, ItemRarity.UNCOMMON,
            effect = StatEffect(socialChange = 10f), isConsumable = false
        ),
        ItemModel("jwl_gold_watch", "Gold Watch", "⌚", "Punctuality and power.", 25000, ItemCategory.JEWELRY, ItemRarity.RARE,
            effect = StatEffect(socialChange = 30f, happinessChange = 10f), isConsumable = false
        ),
        ItemModel("jwl_diamond_ring", "Diamond Ring", "💍", "The ultimate flex.", 120000, ItemCategory.JEWELRY, ItemRarity.EPIC,
            effect = StatEffect(socialChange = 60f, happinessChange = 25f), isConsumable = false
        ),

        // --- 7. PETS (Emotional Support) ---
        ItemModel("pet_cat", "Cyber Cat", "🐈", "Independent and mysterious.", 5000, ItemCategory.PETS, ItemRarity.RARE,
            effect = StatEffect(happinessChange = 15f, stressChange = -20f), isConsumable = false
        ),
        ItemModel("pet_dog", "Neon Dog", "🐕", "Loyal to the last byte.", 6000, ItemCategory.PETS, ItemRarity.RARE,
            effect = StatEffect(happinessChange = 25f, socialChange = 20f), isConsumable = false
        ),
        ItemModel("pet_drone", "Mini Drone", "🚁", "Always watching, always ready.", 15000, ItemCategory.PETS, ItemRarity.EPIC,
            effect = StatEffect(intelligenceChange = 20f, happinessChange = 10f), isConsumable = false
        ),

        // --- 8. ENDGAME LUXURY (Mythic & Prestigious) ---
        ItemModel("home_mega_villa", "Floating Citadel", "☁️", "The ultimate symbol of godhood.", 15000000, ItemCategory.HOMES, ItemRarity.MYTHIC,
            effect = StatEffect(comfortChange = 200f, happinessChange = 200f, socialChange = 200f), isConsumable = false
        ),
        ItemModel("vec_yacht", "Lunar Yacht", "🛳️", "A vessel for the stars.", 8000000, ItemCategory.VEHICLES, ItemRarity.LEGENDARY,
            effect = StatEffect(socialChange = 150f, energyChange = 100f), isConsumable = false
        ),
        ItemModel("jwl_crown_neon", "Neon Overlord Crown", "👑", "Rule the grid.", 5000000, ItemCategory.JEWELRY, ItemRarity.MYTHIC,
            effect = StatEffect(socialChange = 300f, intelligenceChange = 50f), isConsumable = false
        ),
        ItemModel("pet_dragon_mythic", "Ancient Cyber-Drake", "🐲", "A digital relic from the old age.", 20000000, ItemCategory.PETS, ItemRarity.MYTHIC,
            effect = StatEffect(happinessChange = 500f, socialChange = 500f), isConsumable = false
        ),
        ItemModel("cos_aura_gold", "Ascension Aura", "✨", "You radiate success.", 1000000, ItemCategory.AURA, ItemRarity.EPIC,
            effect = StatEffect(socialChange = 50f), isConsumable = false
        )
    )

    fun getItem(id: String): ItemModel? = allItems.find { it.id == id }
}

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

@Serializable
enum class UpgradeCategory {
    ENERGY, HUNGER, HAPPINESS, ECONOMY, SLEEP
}

@Serializable
data class UpgradeModel(
    val id: String,
    val name: String,
    val description: String,
    val category: UpgradeCategory,
    val currentLevel: Int = 0,
    val maxLevel: Int = 10,
    val baseCost: Int = 100,
    val costMultiplier: Float = 1.5f
) {
    fun getNextLevelCost(): Int {
        return (baseCost * Math.pow(costMultiplier.toDouble(), currentLevel.toDouble())).toInt()
    }
    
    fun isMaxLevel(): Boolean = currentLevel >= maxLevel
}

object UpgradeRegistry {
    val allUpgrades = listOf(
        // --- ENERGY ---
        UpgradeModel(
            id = "energy_regen",
            name = "Stellar Recharge",
            description = "Increases energy regeneration while sleeping.",
            category = UpgradeCategory.ENERGY,
            baseCost = 150
        ),
        UpgradeModel(
            id = "energy_decay",
            name = "Endurance Training",
            description = "Reduces energy decay while awake.",
            category = UpgradeCategory.ENERGY,
            baseCost = 200
        ),
        
        // --- HUNGER ---
        UpgradeModel(
            id = "hunger_decay",
            name = "Slow Metabolism",
            description = "Reduces hunger decay rate.",
            category = UpgradeCategory.HUNGER,
            baseCost = 100
        ),
        UpgradeModel(
            id = "food_efficiency",
            name = "Gourmet Palate",
            description = "Increases hunger restored from food.",
            category = UpgradeCategory.HUNGER,
            baseCost = 250
        ),

        // --- HAPPINESS ---
        UpgradeModel(
            id = "happiness_decay",
            name = "Cheerfulness",
            description = "Reduces happiness decay rate.",
            category = UpgradeCategory.HAPPINESS,
            baseCost = 120
        ),
        UpgradeModel(
            id = "toy_efficiency",
            name = "Playful Spirit",
            description = "Increases happiness gain from toys.",
            category = UpgradeCategory.HAPPINESS,
            baseCost = 300
        ),

        // --- ECONOMY ---
        UpgradeModel(
            id = "minigame_reward",
            name = "Lucky Streak",
            description = "Increases coins earned from minigames.",
            category = UpgradeCategory.ECONOMY,
            baseCost = 500,
            costMultiplier = 2.0f
        ),
        UpgradeModel(
            id = "passive_income",
            name = "Investment Fund",
            description = "Earn coins passively over time.",
            category = UpgradeCategory.ECONOMY,
            baseCost = 1000,
            costMultiplier = 2.5f
        ),

        // --- SLEEP ---
        UpgradeModel(
            id = "sleep_quality",
            name = "Deep Sleep",
            description = "Reduces hunger decay while sleeping.",
            category = UpgradeCategory.SLEEP,
            baseCost = 200
        ),
        UpgradeModel(
            id = "fast_wake",
            name = "Quick Starter",
            description = "Increases happiness gain upon waking up.",
            category = UpgradeCategory.SLEEP,
            baseCost = 150
        )
    )
}
