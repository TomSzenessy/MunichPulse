package hackatum.munichpulse.mvp.data.repository

import hackatum.munichpulse.mvp.data.db.MunichPulseDatabase
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
    private lateinit var db: MunichPulseDatabase
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    /**
     * A flow of groups the user belongs to.
     */
    val groups = _groups.asStateFlow()

    /**
     * Initializes the repository with the database.
     * @param database The database instance.
     */
    fun init(database: MunichPulseDatabase) {
        db = database
        refreshGroups()
    }

    private fun refreshGroups() {
        if (!::db.isInitialized) return

        val localGroups = db.schemaQueries.selectAllGroups().executeAsList()
        val allGroups = localGroups.map { localGroup ->
            val members = db.schemaQueries.selectGroupMembers(localGroup.id).executeAsList().map {
                User(it.id, it.name, it.avatarUrl ?: "", it.isLocal)
            }
            val messages = db.schemaQueries.selectGroupMessages(localGroup.id).executeAsList().map {
                ChatMessage(it.id, it.senderId, it.text, it.timestamp)
            }
            Group(localGroup.id, localGroup.eventId, members, messages)
        }
        _groups.value = allGroups
    }

    fun getMyGroups() = groups

    fun joinEvent(eventId: String, user: User) {
        if (!::db.isInitialized) return

        // Simple logic: Check if we are already in a group for this event
        val currentGroups = _groups.value
        val existingGroup = currentGroups.find { it.eventId == eventId }

        if (existingGroup != null) {
            // Add user to existing group in DB
            db.schemaQueries.insertUser(user.id, user.name, user.avatarUrl, user.isLocal)
            db.schemaQueries.insertGroupMember(existingGroup.id, user.id)
        } else {
            // Create new group
            val newGroupId = (currentGroups.size + 1).toString() // Simple ID
            db.schemaQueries.insertGroup(newGroupId, eventId)
            db.schemaQueries.insertUser(user.id, user.name, user.avatarUrl, user.isLocal)
            db.schemaQueries.insertGroupMember(newGroupId, user.id)
        }
        refreshGroups()
    }

    fun sendMessage(groupId: String, text: String, user: User) {
        if (!::db.isInitialized) return

        val newMessageId = currentTimeMillis().toString()
        db.schemaQueries.insertMessage(newMessageId, groupId, user.id, text, currentTimeMillis())
        refreshGroups()
    }
}
