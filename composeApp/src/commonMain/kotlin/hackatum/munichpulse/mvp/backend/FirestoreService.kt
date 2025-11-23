package hackatum.munichpulse.mvp.backend

import hackatum.munichpulse.mvp.data.model.ChatMessage
import hackatum.munichpulse.mvp.data.model.Group
import hackatum.munichpulse.mvp.data.model.User

expect object FirestoreService {
    suspend fun getUserGroups(userId: String): List<Group>
    suspend fun addUserToAvailableGroup(eventId: String, userId: String, maxSize: Int = 5): String
    suspend fun leaveGroup(eventId: String, groupId: String, userId: String)
    suspend fun sendMessage(eventId: String, groupId: String, message: ChatMessage)
    suspend fun getGroup(eventId: String, groupId: String): Group?

    // Real-time flows
    fun getUserGroupsFlow(userId: String): kotlinx.coroutines.flow.Flow<List<Group>>
    fun getMessagesFlow(eventId: String, groupId: String): kotlinx.coroutines.flow.Flow<List<ChatMessage>>

    // User management
    suspend fun upsertUser(uid: String, userData: Map<String, Any>)
    suspend fun getUserData(uid: String): Map<String, Any>?
}
