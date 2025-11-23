package hackatum.munichpulse.mvp.backend

import hackatum.munichpulse.mvp.data.model.ChatMessage
import hackatum.munichpulse.mvp.data.model.Group
import hackatum.munichpulse.mvp.data.model.User

actual object FirestoreService {

    actual suspend fun upsertUser(uid: String, userData: Map<String, Any>) {
        // Mock: No-op
        println("[JS] Mock upsertUser: $uid")
    }

    actual suspend fun getUserData(uid: String): Map<String, Any>? {
        // Mock: Return null or mock data
        println("[JS] Mock getUserData: $uid")
        return null
    }

    actual suspend fun getUserGroups(userId: String): List<Group> {
        // Mock: Return empty list
        println("[JS] Mock getUserGroups: $userId")
        return emptyList()
    }

    actual suspend fun addUserToAvailableGroup(eventId: String, userId: String, maxSize: Int): String {
        // Mock: Return a fake group ID
        println("[JS] Mock addUserToAvailableGroup: $eventId, $userId")
        return "mock_group_id_js"
    }

    actual suspend fun leaveGroup(eventId: String, groupId: String, userId: String) {
        // Mock: No-op
        println("[JS] Mock leaveGroup: $eventId, $groupId, $userId")
    }

    actual suspend fun sendMessage(eventId: String, groupId: String, message: ChatMessage) {
        // Mock: No-op
        println("[JS] Mock sendMessage: ${message.text}")
    }

    actual suspend fun getGroup(eventId: String, groupId: String): Group? {
        // Mock: Return null
        println("[JS] Mock getGroup: $eventId, $groupId")
        return null
    }

    actual fun getUserGroupsFlow(userId: String): kotlinx.coroutines.flow.Flow<List<Group>> {
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }

    actual fun getMessagesFlow(eventId: String, groupId: String): kotlinx.coroutines.flow.Flow<List<ChatMessage>> {
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }
}
