package hackatum.munichpulse.mvp.backend

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.AuthCredential
// import dev.gitlive.firebase.firestore.DocumentReference
// import dev.gitlive.firebase.firestore.firestore
import hackatum.munichpulse.mvp.data.model.Event
import hackatum.munichpulse.mvp.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString


const val USER_COLLECTION: String = "users"
const val USER_UID_PARAM: String = "uid"
const val USER_IS_LOCAL_PARAM: String = "isLocal"
const val USER_NAME_PARAM: String = "name"

// Toggle to route Firebase SDKs to local emulator. Keep false for production.
const val USE_FIREBASE_EMULATOR: Boolean = true

// Host to reach the local Emulator Suite from the app runtime.
const val EMULATOR_IP: String = "131.159.207.110"


const val EVENT_COLLECTION: String = "events"
const val EVENT_NAME_PARAM: String = "title"
const val EVENT_LOCATION_PARAM: String = "location"
const val EVENT_START_DATE_PARAM: String = "startTime"
const val EVENT_GROUPS_SUB_COLLECTION: String = "groups"
const val EVENT_INDIVIDUALS_SUB_COLLECTION: String = "individuals"
const val EVENT_GROUPS_USERS_LIST: String = "users"
// Maintained numeric mirror of users list size to support efficient queries
const val EVENT_GROUPS_USERS_COUNT: String = "usersCount"
const val EVENT_TRACKS_SUB_COLLECTION: String = "tracks"
const val EVENT_TRACK_POSITION_PARAM: String = "gps_coords"
const val EVENT_IMAGE_URL_PARAM: String = "imageUrl"
const val EVENT_FULLNESS_PERCENTAGE_PARAM: String = "fullnessPercentage"
const val EVENT_IS_TRENDING_PARAM: String = "isTrending"


class FirebaseInterface {
    companion object {
        private var INSTANCE: FirebaseInterface? = FirebaseInterface()
        private var initialized: Boolean = false

        fun initializeForWeb() {
            try {
                println("[FirebaseInterface] init start")
                val auth = Firebase.auth
                println("[FirebaseInterface] auth instance acquired: $auth")
                // Use localhost for Web to avoid CORS/Network issues with LAN IP
                if (USE_FIREBASE_EMULATOR) {auth.useEmulator(EMULATOR_IP, 9099)}
                /*
                val db = Firebase.firestore
                println("[FirebaseInterface] firestore instance acquired: $db")
                if (USE_FIREBASE_EMULATOR) {db.useEmulator(EMULATOR_IP, 8080)}
                */
                println("[FirebaseInterface] emulator endpoints configured")
                initialized = true
            } catch (t: Throwable) {
                println("[FirebaseInterface] initialization failed: ${t.message}")
                throw t
            }
        }

        fun getInstance(): FirebaseInterface {
            if (!initialized) {
                // Only use emulators when explicitly enabled
                if (USE_FIREBASE_EMULATOR) {
                    Firebase.auth.useEmulator(EMULATOR_IP, 9099)
                    // Firebase.firestore.useEmulator(EMULATOR_IP, 8080)
                }
                initialized = true
            }
            return INSTANCE!!

        }

    }

    // Function should only ever be used after login!
    fun getUserId(): String {
        return Firebase.auth.currentUser!!.uid
    }

    fun isSignedIn(): Boolean {
        return Firebase.auth.currentUser != null;
    }

    suspend fun userSignIn(name: String, isLocal: Boolean) {
        //  Assume for now the user doesn't exist
        Firebase.auth.signInAnonymously()
        upsertUser(name, isLocal)
    }

    /**
     * Sign in using a Google ID token. This will authenticate the user on this device
     * and upsert the user document so the same data is available on other devices.
     */
    suspend fun signInWithGoogle(idToken: String, name: String? = null, isLocal: Boolean? = null) {
        val credential: AuthCredential = GoogleAuthProvider.credential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
        upsertUser(name, isLocal)
    }

    /**
     * Link the currently signed-in (anonymous) user with Google to upgrade the account.
     * Keeps the same UID so all existing data remains associated with the user.
     */
    suspend fun linkWithGoogle(idToken: String, name: String? = null, isLocal: Boolean? = null) {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // If nobody is signed in, fall back to sign-in with Google
            signInWithGoogle(idToken, name, isLocal)
            return
        }
        val credential: AuthCredential = GoogleAuthProvider.credential(idToken, null)
        currentUser.linkWithCredential(credential)
        upsertUser(name, isLocal)
    }

    /**
     * Upsert user document with optional fields. If name or isLocal are null, the existing values
     * in Firestore are preserved thanks to merge=true.
     */
    suspend fun upsertUser(name: String?, isLocal: Boolean?) {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: return

        if (name != null) {
            try {
                if (currentUser.displayName != name) {
                    currentUser.updateProfile(displayName = name, photoUrl = currentUser.photoURL)
                }
            } catch (e: Throwable) {
                println("Failed to update profile: ${e.message}")
            }
        }

        val userData = buildMap<String, Any> {
            put(USER_UID_PARAM, uid)
            if (isLocal != null) put(USER_IS_LOCAL_PARAM, isLocal)
            if (name != null) put(USER_NAME_PARAM, name)
        }

        FirestoreService.upsertUser(uid, userData)
    }

    /**
     * Retrieve the complete user data for the currently signed-in user.
     */
    suspend fun getCurrentUserData(): User? {
        val auth = Firebase.auth
        val currentUser = auth.currentUser ?: return null
        val uid = currentUser.uid

        val userData = FirestoreService.getUserData(uid)
        
        val nameFromDb = userData?.get(USER_NAME_PARAM) as? String
        val isLocalFromDb = userData?.get(USER_IS_LOCAL_PARAM) as? Boolean

        val finalName = nameFromDb ?: currentUser.displayName ?: ""
        val finalIsLocal = isLocalFromDb ?: false
        val avatarUrl = currentUser.photoURL ?: ""

        return User(
            id = uid,
            name = finalName,
            avatarUrl = avatarUrl,
            isLocal = finalIsLocal
        )
    }


    /**
     * Store a list of users for a specific group inside an event.
     *
     * Path structure (following const vals):
     * events/{eventId}/groups/{groupId} with field "users" as a list of user maps
     * using keys [USER_UID_PARAM], [USER_NAME_PARAM], [USER_IS_LOCAL_PARAM].
     */
    suspend fun addUsersToGroup(eventId: String, groupId: String, users: List<User>) {
        // Not implemented in FirestoreService yet, adding if needed or removing if unused
        // It seems unused by GroupRepository, but let's check.
        // GroupRepository uses joinGroup (addUserToAvailableGroup) and leaveGroup.
    }

    /** Convenience function to add a single user to a group. */
    suspend fun addUserToGroup(eventId: String, groupId: String, user: User) {
        addUsersToGroup(eventId, groupId, listOf(user))
    }

    // Add list of Events to Firebase Database
    suspend fun addEvents(eventList: List<Event>) {
        /*
        val db = Firebase.firestore

        for (event in eventList) {
            val eventMap = hashMapOf<String, Any>(
                EVENT_NAME_PARAM to event.title,
                EVENT_LOCATION_PARAM to event.location,
                EVENT_IS_TRENDING_PARAM to event.isTrending,
                EVENT_IMAGE_URL_PARAM to event.imageUrl,
                EVENT_FULLNESS_PERCENTAGE_PARAM to event.fullnessPercentage,
                EVENT_START_DATE_PARAM to event.startTime
            )
            db.collection(EVENT_COLLECTION).add(eventMap)
            println("Da haben wir ja mal was geschaffT!")
        }
        */
    }

    // return all events in the database
    suspend fun getAllEvents(): List<Event> {
        /*
        val db = Firebase.firestore

        val collection = db.collection(EVENT_COLLECTION).get()
        val eventList = collection.documents.map { doc ->
            try {
                Event(
                    id = doc.id,
                    title = doc.get<String>(EVENT_NAME_PARAM),
                    location = doc.get<String>(EVENT_LOCATION_PARAM),
                    imageUrl = doc.get<String>(EVENT_IMAGE_URL_PARAM),
                    fullnessPercentage = doc.get<Int>(EVENT_FULLNESS_PERCENTAGE_PARAM),
                    isTrending = doc.get<Boolean>(EVENT_IS_TRENDING_PARAM),
                    startTime = doc.get<Int>(EVENT_START_DATE_PARAM)
                )
            }
            catch (e: Exception) {
                null
            }
        }

        return eventList.mapNotNull { it }
    }

    /**
     * Add a user id into the event's individuals sub-collection.
     * Writes events/{eventId}/individuals/{userId} with minimal payload.
     */
    suspend fun addUserToEventIndividuals(eventId: String, userId: String) {
        /*
        val db = Firebase.firestore
        db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_INDIVIDUALS_SUB_COLLECTION)
            .document(userId)
            .set(mapOf(USER_UID_PARAM to userId), merge = true)
        */
    }

    /**
     * Add the user to any available group (users list size < maxSize). If none exists, a new
     * group is created. The group document maintains a simple list of user ids under "users".
     * Returns the groupId the user has been added to.
     */
    suspend fun addUserToAvailableGroup(eventId: String, userId: String, maxSize: Int = 5): String {
        return FirestoreService.addUserToAvailableGroup(eventId, userId, maxSize)
    }

    /**
     * Add the user to the specified group users list (stored as List<String> of UIDs).
     * Creates the group document if it doesn't exist yet.
     */
    suspend fun addUserToGroup(eventId: String, groupId: String, userId: String) {
        /*
        val db = Firebase.firestore
        val groupRef = db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_GROUPS_SUB_COLLECTION)
            .document(groupId)

        val currentUsers = try {
            @Suppress("UNCHECKED_CAST")
            (groupRef.get().get(EVENT_GROUPS_USERS_LIST) as? List<String>) ?: emptyList()
        } catch (_: Throwable) { emptyList() }

        if (!currentUsers.contains(userId)) {
            val updated = currentUsers + userId
            groupRef.set(
                mapOf(
                    EVENT_GROUPS_USERS_LIST to updated,
                    EVENT_GROUPS_USERS_COUNT to updated.size
                ),
                merge = true
            )
        }
        */
    }

    /**
     * Remove the user from the specified group.
     */
    suspend fun leaveGroup(eventId: String, groupId: String, userId: String) {
        FirestoreService.leaveGroup(eventId, groupId, userId)
    }

    /**
     * Get a specific group by ID.
     */
    suspend fun getGroup(eventId: String, groupId: String): hackatum.munichpulse.mvp.data.model.Group? {
        return FirestoreService.getGroup(eventId, groupId)
    }

    /**
     * Get all groups the user is a member of.
     * Uses a Collection Group Query.
     */
    suspend fun getUserGroups(userId: String): List<hackatum.munichpulse.mvp.data.model.Group> {
        return FirestoreService.getUserGroups(userId)
    }

    /**
     * Helper to get user by ID.
     */
    suspend fun getUserById(uid: String): User? {
        val userData = FirestoreService.getUserData(uid) ?: return null
        val name = userData[USER_NAME_PARAM] as? String ?: "Unknown"
        val isLocal = userData[USER_IS_LOCAL_PARAM] as? Boolean ?: false
        return User(id = uid, name = name, avatarUrl = "", isLocal = isLocal)
    }

    /**
     * Send a chat message to a group.
     * Path: events/{eventId}/groups/{groupId}/messages/{messageId}
     */
    suspend fun sendMessage(eventId: String, groupId: String, message: hackatum.munichpulse.mvp.data.model.ChatMessage) {
        FirestoreService.sendMessage(eventId, groupId, message)
    }
}
