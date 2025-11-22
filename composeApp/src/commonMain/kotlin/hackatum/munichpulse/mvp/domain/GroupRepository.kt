package hackatum.munichpulse.mvp.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object GroupRepository {
    private val _groups = MutableStateFlow<List<Group>>(listOf(
        Group(
            id = "1",
            eventId = "1", // Tollwood
            members = listOf(
                User("1", "Alice", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", true),
                User("2", "Bob", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", false),
                User("3", "Charlie", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", true)
            ),
            chatMessages = listOf(
                ChatMessage("1", "1", "Hey everyone! Who's excited for Tollwood?", 1700000000000),
                ChatMessage("2", "2", "Me! Can't wait for the food.", 1700000060000)
            )
        ),
        Group(
            id = "2",
            eventId = "3", // Open Air Kino
            members = listOf(
                User("4", "David", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", true),
                User("5", "Eve", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", false)
            ),
            chatMessages = emptyList()
        )
    ))
    val groups = _groups.asStateFlow()

    fun getMyGroups() = groups

    fun joinEvent(eventId: String, user: User) {
        _groups.update { currentGroups ->
            // Find a group for this event that has space (< 6 members)
            val existingGroup = currentGroups.find { it.eventId == eventId && it.members.size < 6 }
            
            if (existingGroup != null) {
                // Add to existing group if not already a member
                if (existingGroup.members.none { it.id == user.id }) {
                    val updatedGroup = existingGroup.copy(members = existingGroup.members + user)
                    currentGroups.map { if (it.id == existingGroup.id) updatedGroup else it }
                } else {
                    currentGroups // User already in group
                }
            } else {
                // Create new group
                val newGroup = Group(
                    id = (currentGroups.size + 1).toString(),
                    eventId = eventId,
                    members = listOf(user),
                    chatMessages = emptyList()
                )
                currentGroups + newGroup
            }
        }
    }

    fun sendMessage(groupId: String, text: String, user: User) {
        _groups.update { currentGroups ->
            currentGroups.map { group ->
                if (group.id == groupId) {
                    val newMessage = ChatMessage(
                        id = (group.chatMessages.size + 1).toString(), // Simple ID generation
                        senderId = user.id,
                        text = text,
                        timestamp = 0L // Placeholder timestamp
                    )
                    group.copy(chatMessages = group.chatMessages + newMessage)
                } else {
                    group
                }
            }
        }
    }
}
