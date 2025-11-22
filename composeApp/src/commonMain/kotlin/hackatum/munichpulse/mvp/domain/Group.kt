package hackatum.munichpulse.mvp.domain

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String,
    val eventId: String,
    val members: List<User>,
    val chatMessages: List<ChatMessage> = emptyList()
)

@Serializable
data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long
)
