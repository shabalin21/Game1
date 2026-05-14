package com.example.myapplication.ui.screen.casino.model

data class GameRule(
    val title: String,
    val description: String,
    val payouts: List<Pair<String, String>>
)

object CasinoRules {
    val blackjack = GameRule(
        title = "BLACKJACK",
        description = "Beat the dealer by getting a count as close to 21 as possible. Dealer stays on 17.",
        payouts = listOf(
            "Natural Blackjack" to "3:2 (2.5x Total)",
            "Standard Win" to "2.0x",
            "Push (Draw)" to "1.0x (Bet Return)",
            "Bust / Loss" to "0x"
        )
    )

    val slots = GameRule(
        title = "SLOTS",
        description = "Align three fragments to unlock credit surges. Rarity of symbols determines payout.",
        payouts = listOf(
            "Common (🍒/🍋/🍇)" to "2x - 5x",
            "Rare (🔔/💎)" to "10x - 25x",
            "Gold (7️⃣)" to "50x",
            "Glitch (👾)" to "100x",
            "Mythic (👑)" to "500x",
            "Partial Match (2/3)" to "0.2x Multiplier"
        )
    )

    val coinflip = GameRule(
        title = "BINARY_FLIP",
        description = "Predict the outcome of a quantum coin oscillation. Consecutive wins build streak multipliers.",
        payouts = listOf(
            "Win" to "2.0x",
            "Streak Bonus" to "+0.1x per win",
            "Loss" to "0x"
        )
    )

    val crash = GameRule(
        title = "MARKET_CRASH",
        description = "A multiplier rises from 1.0x. Cash out before the market collapses. High risk, high reward.",
        payouts = listOf(
            "Cash Out" to "Current Multiplier",
            "Crash" to "Total Loss",
            "Insta-Crash (1.0x)" to "3% Probability"
        )
    )

    val plinko = GameRule(
        title = "PLINKO",
        description = "Drop a neural ball through the grid. Multipliers at the bottom determine your payout based on path probability.",
        payouts = listOf(
            "Low Risk" to "1.0x - 5.0x",
            "Medium Risk" to "0.5x - 15.0x",
            "High Risk" to "0x - 50.0x"
        )
    )
}
