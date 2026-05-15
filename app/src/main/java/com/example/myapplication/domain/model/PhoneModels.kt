package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PhoneState(
    val contacts: List<Contact> = emptyList(),
    val threads: List<MessageThread> = emptyList(),
    val notifications: List<PhoneNotification> = emptyList()
)

@Serializable
data class Contact(
    val id: String,
    val name: String,
    val relation: RelationType = RelationType.ACQUAINTANCE,
    val avatar: String = "👤",
    val lastInteractionTimestamp: Long = 0,
    val personality: NpcPersonality = NpcPersonality()
)

@Serializable
enum class RelationType {
    ACQUAINTANCE, FRIEND, CLOSE_FRIEND, BEST_FRIEND, COLLEAGUE, BOSS, RIVAL
}

@Serializable
data class NpcPersonality(
    val formality: Float = 0.5f, // 0 to 1
    val excitement: Float = 0.5f, // 0 to 1
    val frequency: Float = 0.5f   // How often they message
)

@Serializable
data class MessageThread(
    val contactId: String,
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0,
    val lastMessageTimestamp: Long = 0
)

@Serializable
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String, // "player" or contactId
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val tone: MessageTone = MessageTone.NEUTRAL
)

@Serializable
enum class MessageTone {
    NEUTRAL, HAPPY, ANGRY, URGENT, SAD, PROFESSIONAL
}

@Serializable
data class PhoneNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
enum class NotificationType {
    MESSAGE, WORK, WORLD_EVENT, RELATIONSHIP, BILL
}
