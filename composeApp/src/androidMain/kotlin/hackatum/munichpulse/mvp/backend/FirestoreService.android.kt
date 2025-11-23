package hackatum.munichpulse.mvp.backend

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.QuerySnapshot
import hackatum.munichpulse.mvp.data.model.ChatMessage
import hackatum.munichpulse.mvp.data.model.Group
import hackatum.munichpulse.mvp.data.model.User
import hackatum.munichpulse.mvp.backend.EVENT_COLLECTION
import hackatum.munichpulse.mvp.backend.EVENT_GROUPS_SUB_COLLECTION
import hackatum.munichpulse.mvp.backend.EVENT_GROUPS_USERS_LIST
import hackatum.munichpulse.mvp.backend.EVENT_GROUPS_USERS_COUNT
import hackatum.munichpulse.mvp.backend.USER_COLLECTION
import hackatum.munichpulse.mvp.backend.USER_NAME_PARAM
import hackatum.munichpulse.mvp.backend.USER_IS_LOCAL_PARAM
import hackatum.munichpulse.mvp.backend.USE_FIREBASE_EMULATOR
import hackatum.munichpulse.mvp.backend.USE_FIREBASE_EMULATOR
import hackatum.munichpulse.mvp.backend.EMULATOR_IP
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.awaitAll

actual object FirestoreService {

    init {
        if (USE_FIREBASE_EMULATOR) {
            try {
                Firebase.firestore.useEmulator(EMULATOR_IP, 8080)
            } catch (e: Exception) {
                println("Firestore emulator already configured or failed: ${e.message}")
            }
        }
    }

    actual suspend fun upsertUser(uid: String, userData: Map<String, Any>) {
        val db = Firebase.firestore
        db.collection(USER_COLLECTION).document(uid).set(userData, merge = true)
    }

    actual suspend fun getUserData(uid: String): Map<String, Any>? {
        val db = Firebase.firestore
        val snapshot = db.collection(USER_COLLECTION).document(uid).get()
        if (!snapshot.exists) return null
        
        // Convert snapshot data to Map
        // Note: gitlive firestore returns generic objects, we might need to be careful with types
        // For simplicity, we extract known fields
        val name = snapshot.get(USER_NAME_PARAM) as? String
        val isLocal = snapshot.get(USER_IS_LOCAL_PARAM) as? Boolean
        
        val map = mutableMapOf<String, Any>()
        if (name != null) map[USER_NAME_PARAM] = name
        if (isLocal != null) map[USER_IS_LOCAL_PARAM] = isLocal
        return map
    }

    actual suspend fun getUserGroups(userId: String): List<Group> {
        val db = Firebase.firestore
        // Note: This requires an index on 'users' array-contains.
        val querySnapshot = db.collectionGroup(EVENT_GROUPS_SUB_COLLECTION)
            .where { EVENT_GROUPS_USERS_LIST contains userId }
            .get()

        return querySnapshot.documents.mapNotNull { doc ->
            val eventId = doc.reference.parent.parent?.id ?: return@mapNotNull null
            
            val userIds = try {
                @Suppress("UNCHECKED_CAST")
                (doc.get(EVENT_GROUPS_USERS_LIST) as? List<String>) ?: emptyList()
            } catch (_: Throwable) { emptyList() }

            val members = userIds.mapNotNull { uid ->
                getUserByIdInternal(uid)
            }

            Group(
                id = doc.id,
                eventId = eventId,
                members = members,
                chatMessages = emptyList() // Fetch messages separately if needed
            )
        }
    }

    actual suspend fun addUserToAvailableGroup(eventId: String, userId: String, maxSize: Int): String {
        val db = Firebase.firestore
        val groupsColl = db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_GROUPS_SUB_COLLECTION)

        val querySnapshot = groupsColl
            .where { EVENT_GROUPS_USERS_COUNT lessThan maxSize }
            .limit(1)
            .get()

        var targetGroupRef: DocumentReference? = null
        var users: List<String> = emptyList()
        if (querySnapshot.documents.isNotEmpty()) {
            val doc = querySnapshot.documents.first()
            users = try {
                @Suppress("UNCHECKED_CAST")
                (doc.get(EVENT_GROUPS_USERS_LIST) as? List<String>) ?: emptyList()
            } catch (_: Throwable) { emptyList() }
            
            if (users.contains(userId)) {
                return doc.id
            }
            targetGroupRef = doc.reference
        }

        if (targetGroupRef == null) {
            val newDocRef = groupsColl.add(
                mapOf(
                    EVENT_GROUPS_USERS_LIST to listOf<String>(),
                    EVENT_GROUPS_USERS_COUNT to 0
                )
            )
            targetGroupRef = newDocRef
        }

        if (!users.contains(userId)) {
            val updated = users + userId
            targetGroupRef.set(
                mapOf(
                    EVENT_GROUPS_USERS_LIST to updated,
                    EVENT_GROUPS_USERS_COUNT to updated.size
                ),
                merge = true
            )
        }

        return targetGroupRef.id
    }

    actual suspend fun leaveGroup(eventId: String, groupId: String, userId: String) {
        val db = Firebase.firestore
        val groupRef = db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_GROUPS_SUB_COLLECTION)
            .document(groupId)

        val currentUsers = try {
            @Suppress("UNCHECKED_CAST")
            (groupRef.get().get(EVENT_GROUPS_USERS_LIST) as? List<String>) ?: emptyList()
        } catch (_: Throwable) { emptyList() }

        if (currentUsers.contains(userId)) {
            val updated = currentUsers - userId
            groupRef.set(
                mapOf(
                    EVENT_GROUPS_USERS_LIST to updated,
                    EVENT_GROUPS_USERS_COUNT to updated.size
                ),
                merge = true
            )
        }
    }

    actual suspend fun sendMessage(eventId: String, groupId: String, message: ChatMessage) {
        val db = Firebase.firestore
        db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_GROUPS_SUB_COLLECTION)
            .document(groupId)
            .collection("messages")
            .document(message.id)
            .set(message)
    }

    actual suspend fun getGroup(eventId: String, groupId: String): Group? {
        val db = Firebase.firestore
        val doc = db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_GROUPS_SUB_COLLECTION)
            .document(groupId)
            .get()

        if (!doc.exists) return null

        val userIds = try {
            @Suppress("UNCHECKED_CAST")
            (doc.get(EVENT_GROUPS_USERS_LIST) as? List<String>) ?: emptyList()
        } catch (_: Throwable) { emptyList() }

        val members = userIds.mapNotNull { uid ->
            getUserByIdInternal(uid)
        }

        return Group(
            id = doc.id,
            eventId = eventId,
            members = members,
            chatMessages = emptyList()
        )
    }

    actual fun getUserGroupsFlow(userId: String): kotlinx.coroutines.flow.Flow<List<Group>> {
        val db = Firebase.firestore
        return db.collectionGroup(EVENT_GROUPS_SUB_COLLECTION)
            .where { EVENT_GROUPS_USERS_LIST contains userId }
            .snapshots
            .map { querySnapshot ->
                kotlinx.coroutines.coroutineScope {
                    val scope = this
                    val deferreds: List<kotlinx.coroutines.Deferred<Group?>> = querySnapshot.documents.map { doc ->
                        scope.async {
                            val innerScope = this
                            val eventId = doc.reference.parent.parent?.id ?: return@async null
                            
                            val userIds = try {
                                @Suppress("UNCHECKED_CAST")
                                (doc.get(EVENT_GROUPS_USERS_LIST) as? List<String>) ?: emptyList()
                            } catch (_: Throwable) { emptyList() }

                            // Fetch members concurrently
                            val members = userIds.map { uid ->
                                innerScope.async { getUserByIdInternal(uid) }
                            }.awaitAll().filterNotNull()

                            Group(
                                id = doc.id,
                                eventId = eventId,
                                members = members,
                                chatMessages = emptyList()
                            )
                        }
                    }
                    deferreds.awaitAll().filterNotNull()
                }
            }
    }

    actual fun getMessagesFlow(eventId: String, groupId: String): kotlinx.coroutines.flow.Flow<List<ChatMessage>> {
        val db = Firebase.firestore
        return db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_GROUPS_SUB_COLLECTION)
            .document(groupId)
            .collection("messages")
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.map { doc ->
                    doc.data() // Assumes ChatMessage can be deserialized directly or we map manually
                }
            }
    }

    private suspend fun getUserByIdInternal(uid: String): User? {
        val userData = getUserData(uid) ?: return null
        val name = userData[USER_NAME_PARAM] as? String ?: "Unknown"
        val isLocal = userData[USER_IS_LOCAL_PARAM] as? Boolean ?: false
        
        return User(
            id = uid,
            name = name,
            avatarUrl = "", // Avatar not stored in Firestore currently
            isLocal = isLocal
        )
    }
}
