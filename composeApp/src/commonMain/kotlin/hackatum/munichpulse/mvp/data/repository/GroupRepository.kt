package hackatum.munichpulse.mvp.data.repository

import hackatum.munichpulse.mvp.data.model.ChatMessage
import hackatum.munichpulse.mvp.data.model.Group
import hackatum.munichpulse.mvp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import hackatum.munichpulse.mvp.currentTimeMillis

/**
 * Repository for managing group and chat data.
 * Handles fetching groups, joining events, and sending messages.
 */
object GroupRepository {
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    /**
     * A flow of groups the user belongs to.
     */
    val groups = _groups.asStateFlow()

    /**
     * Initializes the repository.
     */
    fun init() {
        refreshGroups()
    }

    private fun refreshGroups() {
        // Mock data
        val mockGroups = listOf(
            Group(
                id = "1",
                eventId = "event1",
                members = listOf(
                    User("u1", "Alice", "", true),
                    User("u2", "Bob", "", false)
                ),
                chatMessages = listOf(
                    ChatMessage("m1", "u1", "Hey everyone!", currentTimeMillis() - 10000),
                    ChatMessage("m2", "u2", "Hello!", currentTimeMillis() - 5000)
                )
            ),
            Group(
                id = "2",
                eventId = "event2",
                members = listOf(
                    User("u3", "Charlie", "", true),
                    User("u4", "Dave", "", false)
                ),
                chatMessages = listOf(
                    ChatMessage("m3", "u3", "Anyone going to the concert?", currentTimeMillis() - 20000)
                )
            )
        )
        _groups.value = mockGroups
    }

    fun getMyGroups() = groups

    fun joinEvent(eventId: String, user: User) {
        // Mock implementation: Add user to a group for this event
        val currentGroups = _groups.value.toMutableList()
        val existingGroupIndex = currentGroups.indexOfFirst { it.eventId == eventId }

        if (existingGroupIndex != -1) {
            val group = currentGroups[existingGroupIndex]
            val updatedMembers = group.members + user
            currentGroups[existingGroupIndex] = group.copy(members = updatedMembers)
        } else {
            val newGroup = Group(
                id = (currentGroups.size + 1).toString(),
                eventId = eventId,
                members = listOf(user)
            )
            currentGroups.add(newGroup)
        }
        _groups.value = currentGroups
    }

    fun sendMessage(groupId: String, text: String, user: User) {
        val currentGroups = _groups.value.toMutableList()
        val groupIndex = currentGroups.indexOfFirst { it.id == groupId }

        if (groupIndex != -1) {
            val group = currentGroups[groupIndex]
            val newMessage = ChatMessage(
                id = currentTimeMillis().toString(),
                senderId = user.id,
                text = text,
                timestamp = currentTimeMillis()
            )
            val updatedMessages = group.chatMessages + newMessage
            currentGroups[groupIndex] = group.copy(chatMessages = updatedMessages)
            _groups.value = currentGroups
        }
    }
}
