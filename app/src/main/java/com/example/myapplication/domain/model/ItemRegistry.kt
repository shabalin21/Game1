package com.example.myapplication.domain.model

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
