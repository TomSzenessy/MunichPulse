package hackatum.munichpulse.mvp.data.repository

import hackatum.munichpulse.mvp.backend.FirebaseInterface
import hackatum.munichpulse.mvp.backend.FirestoreService
import hackatum.munichpulse.mvp.data.model.ChatMessage
import hackatum.munichpulse.mvp.data.model.Group
import hackatum.munichpulse.mvp.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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

    private var refreshJob: Job? = null

    /**
     * Initializes the repository.
     */
    fun init() {
        startObservingGroups()
    }

    private fun startObservingGroups() {
        refreshJob?.cancel()
        refreshJob = CoroutineScope(Dispatchers.Default).launch {
            // Wait for sign in or observe auth state?
            // For now, simple check. Ideally we observe auth state.
            while (isActive) {
                if (FirebaseInterface.getInstance().isSignedIn()) {
                    val userId = FirebaseInterface.getInstance().getUserId()
                    FirestoreService.getUserGroupsFlow(userId).collect { fetchedGroups ->
                        _groups.value = fetchedGroups
                    }
                    break // Once collecting, we don't need to loop unless auth changes/flow completes
                }
                delay(1000) // Wait for auth
            }
        }
    }

    fun getMyGroups() = groups

    fun getMessages(eventId: String, groupId: String): kotlinx.coroutines.flow.Flow<List<ChatMessage>> {
        return FirestoreService.getMessagesFlow(eventId, groupId)
    }

    suspend fun joinGroup(eventId: String) {
        if (!FirebaseInterface.getInstance().isSignedIn()) return
        val userId = FirebaseInterface.getInstance().getUserId()
        FirebaseInterface.getInstance().addUserToAvailableGroup(eventId, userId)
        // Flow will update automatically
    }

    suspend fun leaveGroup(eventId: String, groupId: String) {
        if (!FirebaseInterface.getInstance().isSignedIn()) return
        val userId = FirebaseInterface.getInstance().getUserId()
        FirebaseInterface.getInstance().leaveGroup(eventId, groupId, userId)
        // Flow will update automatically
    }

    suspend fun sendMessage(eventId: String, groupId: String, text: String, user: User) {
        val message = ChatMessage(
            id = currentTimeMillis().toString(),
            senderId = user.id,
            text = text,
            timestamp = currentTimeMillis()
        )
        FirebaseInterface.getInstance().sendMessage(eventId, groupId, message)
    }
    
    // refreshGroups is no longer needed with Flow
}
