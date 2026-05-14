package com.example.myapplication.domain.simulation

import com.example.myapplication.domain.model.TimeOfDay
import com.example.myapplication.domain.model.Weather
import com.example.myapplication.domain.model.WorldState
import com.example.myapplication.domain.repository.WorldRepository
import com.example.myapplication.domain.event.GameplayEventManager
import com.example.myapplication.domain.event.GameplayEvent
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

import kotlinx.coroutines.flow.first

import com.example.myapplication.domain.event.WorldEventType
import com.example.myapplication.domain.event.WorldEventImpact

import com.example.myapplication.domain.social.SocialManager
import com.example.myapplication.domain.work.engine.JobManager

@Singleton
class WorldManager @Inject constructor(
    private val worldRepository: WorldRepository,
    private val eventManager: GameplayEventManager,
    private val socialManager: SocialManager,
    private val jobManager: JobManager
) {
    // 1 in-game day = 24 minutes real-time (each hour is 1 minute)
    private val MINUTES_PER_HOUR = 1
    
    private val eventRegistry = listOf(
        WorldEventData("crypto_boom", "CRYPTO_BOOM", "The market is surging! BTC value is pumping.", WorldEventType.CRYPTO, WorldEventImpact.POSITIVE),
        WorldEventData("market_crash", "MARKET_CRASH", "Panic selling in the sectors. Crypto is down.", WorldEventType.CRYPTO, WorldEventImpact.NEGATIVE),
        WorldEventData("casino_happy_hour", "HAPPY_HOUR", "Luck is in the air. Casino bonuses active.", WorldEventType.GAMBLING, WorldEventImpact.POSITIVE),
        WorldEventData("police_raid", "SECURITY_SWEEP", "Heavy police presence. Work efficiency increased.", WorldEventType.SOCIAL, WorldEventImpact.NEUTRAL),
        WorldEventData("inheritance", "UNEXPECTED_GIFT", "A distant relative left you a small sum.", WorldEventType.FINANCIAL, WorldEventImpact.LEGENDARY),
        WorldEventData("tax_increase", "SERVICE_FEE", "City maintenance fees have increased temporarily.", WorldEventType.FINANCIAL, WorldEventImpact.NEGATIVE)
    )

    suspend fun tick(currentTimeMillis: Long) {
        val currentState = worldRepository.getWorldState().first()
        
        // Update Time of Day
        val totalMinutesInDay = 24 * MINUTES_PER_HOUR
        val currentMinuteOfDay = ((currentTimeMillis / 1000 / 60) % totalMinutesInDay).toInt()
        
        val newTimeOfDay = when (currentMinuteOfDay) {
            in 5 until 8 -> TimeOfDay.DAWN
            in 8 until 18 -> TimeOfDay.DAY
            in 18 until 21 -> TimeOfDay.DUSK
            else -> TimeOfDay.NIGHT
        }

        if (newTimeOfDay != currentState.timeOfDay) {
            Timber.i("World: Time transition to $newTimeOfDay")
            worldRepository.updateWorldState { it.copy(timeOfDay = newTimeOfDay) }
        }

        // Random Weather Changes (Every 3-4 hours on average)
        if (Random.nextFloat() < 0.002f) {
            val newWeather = Weather.entries.random()
            if (newWeather != currentState.weather) {
                Timber.i("World: Weather changed to $newWeather")
                worldRepository.updateWorldState { it.copy(weather = newWeather) }
            }
        }

        // Trigger Random Events
        if (Random.nextFloat() < 0.005f) {
            triggerRandomEvent()
            jobManager.updateMarket()
        }

        // Ambient NPC Posts
        if (Random.nextFloat() < 0.002f) {
            socialManager.generateNpcPost(null)
        }
        
        worldRepository.updateWorldState { it.copy(lastUpdateTimestamp = currentTimeMillis) }
    }

    private suspend fun triggerRandomEvent() {
        val eventData = eventRegistry.random()
        Timber.i("World: Triggering event ${eventData.id}")
        
        val event = GameplayEvent.WorldEventTriggered(
            id = eventData.id,
            title = eventData.title,
            description = eventData.description,
            type = eventData.type,
            impact = eventData.impact
        )
        
        eventManager.dispatch(event)
        
        // Generate NPC post reacting to the event
        socialManager.generateNpcPost(event)
    }
}

data class WorldEventData(
    val id: String,
    val title: String,
    val description: String,
    val type: WorldEventType,
    val impact: WorldEventImpact
)
