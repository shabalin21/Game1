package com.example.myapplication.domain.simulation.phone

import com.example.myapplication.core.EventBus
import com.example.myapplication.core.KernelSystem
import com.example.myapplication.domain.event.GameplayEvent
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.repository.PetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PhoneManager @Inject constructor(
    private val eventBus: EventBus,
    private val petRepository: PetRepository
) : KernelSystem {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onBoot() {
        Timber.i("PhoneManager: Booting...")
        eventBus.events
            .onEach { event ->
                if (event is GameplayEvent.SimulationTick) {
                    handleSimulationTick(event.timestamp)
                }
            }
            .launchIn(scope)
    }

    override fun onShutdown() {
        Timber.i("PhoneManager: Shutting down...")
    }

    private suspend fun handleSimulationTick(timestamp: Long) {
        // Probability of receiving a random message
        if (Random.nextFloat() < 0.05f) { // 5% chance per tick
            generateRandomMessage(timestamp)
        }
    }

    private suspend fun generateRandomMessage(timestamp: Long) {
        val pet = petRepository.getPetState().first() ?: return
        val currentPhone = pet.phone
        
        // Ensure we have some contacts
        if (currentPhone.contacts.isEmpty()) {
            initializeDefaultContacts(pet)
            return
        }

        val contact = currentPhone.contacts.random()
        val text = getRandomText(contact)
        
        val newMessage = Message(
            senderId = contact.id,
            text = text,
            timestamp = timestamp,
            tone = getToneForPersonality(contact.personality)
        )

        val updatedThreads = currentPhone.threads.toMutableList()
        val threadIndex = updatedThreads.indexOfFirst { it.contactId == contact.id }
        
        if (threadIndex != -1) {
            val thread = updatedThreads[threadIndex]
            updatedThreads[threadIndex] = thread.copy(
                messages = thread.messages + newMessage,
                unreadCount = thread.unreadCount + 1,
                lastMessageTimestamp = timestamp
            )
        } else {
            updatedThreads.add(
                MessageThread(
                    contactId = contact.id,
                    messages = listOf(newMessage),
                    unreadCount = 1,
                    lastMessageTimestamp = timestamp
                )
            )
        }

        val newNotification = PhoneNotification(
            title = "Message from ${contact.name}",
            body = text,
            type = NotificationType.MESSAGE,
            timestamp = timestamp
        )

        val updatedPhone = currentPhone.copy(
            threads = updatedThreads,
            notifications = currentPhone.notifications + newNotification
        )

        petRepository.savePetState(pet.copy(phone = updatedPhone))
        Timber.i("PhoneManager: New message received from ${contact.name}")
    }

    private suspend fun initializeDefaultContacts(pet: PetModel) {
        val defaults = listOf(
            Contact("boss_id", "The Boss", RelationType.BOSS, "💼", personality = NpcPersonality(formality = 0.9f)),
            Contact("mom_id", "Mom", RelationType.CLOSE_FRIEND, "👵", personality = NpcPersonality(formality = 0.2f, excitement = 0.8f)),
            Contact("friend_id", "Alex", RelationType.FRIEND, "🎮", personality = NpcPersonality(formality = 0.4f, excitement = 0.6f))
        )
        
        val updatedPhone = pet.phone.copy(contacts = defaults)
        petRepository.savePetState(pet.copy(phone = updatedPhone))
    }

    private fun getRandomText(contact: Contact): String {
        return when (contact.relation) {
            RelationType.BOSS -> listOf("Project status?", "Meeting in 5.", "Efficiency is down.", "Good work today.").random()
            RelationType.FRIEND -> listOf("Wanna hang?", "Check this out!", "Had a great time.", "Lol same.").random()
            else -> "Hey there!"
        }
    }

    private fun getToneForPersonality(personality: NpcPersonality): MessageTone {
        return when {
            personality.formality > 0.8f -> MessageTone.PROFESSIONAL
            personality.excitement > 0.8f -> MessageTone.HAPPY
            else -> MessageTone.NEUTRAL
        }
    }
}
