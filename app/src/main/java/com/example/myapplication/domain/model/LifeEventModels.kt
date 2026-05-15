package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LifeEvent(
    val id: String,
    val title: String,
    val description: String,
    val type: LifeEventType,
    val probability: Float, // 0 to 1 per tick
    val effect: LifeEventEffect
)

@Serializable
enum class LifeEventType {
    FINANCIAL, SOCIAL, HEALTH, ENVIRONMENTAL
}

@Serializable
data class LifeEventEffect(
    val coinDelta: Int = 0,
    val statEffect: StatEffect = StatEffect(),
    val moodModifier: MoodModifier? = null
)

object LifeEventRegistry {
    val allEvents = listOf(
        LifeEvent("bill_late", "Unexpected Bill", "A forgotten utility bill arrived.", LifeEventType.FINANCIAL, 0.01f, 
            LifeEventEffect(coinDelta = -150, statEffect = StatEffect(stressChange = 10f))
        ),
        LifeEvent("neighbor_noise", "Noisy Neighbors", "Party next door kept you awake.", LifeEventType.ENVIRONMENTAL, 0.02f, 
            LifeEventEffect(statEffect = StatEffect(energyChange = -10f, stressChange = 5f))
        ),
        LifeEvent("bonus_income", "Tax Refund", "Unexpected credits in your account.", LifeEventType.FINANCIAL, 0.005f, 
            LifeEventEffect(coinDelta = 500, statEffect = StatEffect(happinessChange = 15f))
        ),
        LifeEvent("social_invite", "Social Invitation", "A friend wants to hang out.", LifeEventType.SOCIAL, 0.015f, 
            LifeEventEffect(statEffect = StatEffect(socialChange = 20f, happinessChange = 10f))
        )
    )
}
