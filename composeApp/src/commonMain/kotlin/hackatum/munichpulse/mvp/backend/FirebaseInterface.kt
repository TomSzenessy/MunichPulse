package hackatum.munichpulse.mvp.backend

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.firestore
import hackatum.munichpulse.mvp.data.model.Event
import hackatum.munichpulse.mvp.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val USER_COLLECTION: String = "users"
const val USER_UID_PARAM: String = "uid"
const val USER_IS_LOCAL_PARAM: String = "isLocal"
const val USER_NAME_PARAM: String = "name"

// Toggle to route Firebase SDKs to local emulator. Keep false for production.
const val USE_FIREBASE_EMULATOR: Boolean = true

// Host to reach the local Emulator Suite from the app runtime.
const val EMULATOR_IP: String = "131.159.207.110"


const val EVENT_COLLECTION: String = "events"
const val EVENT_NAME_PARAM: String = "name"
const val EVENT_LOCATION_PARAM: String = "location"
const val EVENT_DATA_BEGIN_PARAM: String = "begin_date"
const val EVENT_GROUPS_SUB_COLLECTION: String = "groups"
const val EVENT_INDIVIDUALS_SUB_COLLECTION: String = "individuals"
const val EVENT_GROUPS_USERS_LIST: String = "users"
// Maintained numeric mirror of users list size to support efficient queries
const val EVENT_GROUPS_USERS_COUNT: String = "usersCount"
const val EVENT_TRACKS_SUB_COLLECTION: String = "tracks"
const val EVENT_TRACK_POSITION_PARAM: String = "gps_coords"
const val EVENT_IMAGE_URL_PARAM: String = "image_url"
const val EVENT_FULLNESS_PERCENTAGE_PARAM: String = "fullness_percentage"
const val EVENT_IS_TRENDING_PARAM: String = "is_trending"


class FirebaseInterface {
    companion object {
        private var INSTANCE: FirebaseInterface = FirebaseInterface()
        private var initialized: Boolean = false

        fun getInstance(): FirebaseInterface {
            if (!initialized) {
                // Only use emulators when explicitly enabled
                if (USE_FIREBASE_EMULATOR) {
                    Firebase.auth.useEmulator(EMULATOR_IP, 9099)
                    Firebase.firestore.useEmulator(EMULATOR_IP, 8080)
                }
                initialized = true
            }
            return INSTANCE
        }
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

        // Ensure we have an authenticated user context
        val currentUser = auth.currentUser

        // If still no user, we cannot proceed
        val uid = currentUser?.uid ?: return

        // Also update the Firebase Auth profile display name when provided
        if (name != null) {
            try {
                // Only update if different to avoid unnecessary network calls
                if (currentUser.displayName != name) {
                    currentUser.updateProfile(displayName = name)
                }
            } catch (_: Throwable) {
                // Ignore failures for profile update so Firestore upsert still succeeds
            }
        }

        val userData = buildMap<String, Any> {
            put(USER_UID_PARAM, uid)
            if (isLocal != null) put(USER_IS_LOCAL_PARAM, isLocal)
            if (name != null) put(USER_NAME_PARAM, name)
        }

        // Upsert user document so it works for anonymous and Google-authenticated users
        val db = Firebase.firestore
        db.collection(USER_COLLECTION).document(uid).set(userData, merge = true)
    }

    /**
     * Retrieve the complete user data for the currently signed-in user by combining
     * Firebase Auth profile info with the Firestore user document (users/{uid}).
     *
     * Returns null if no user is currently signed in.
     */
    suspend fun getCurrentUserData(): User? {
        val auth = Firebase.auth
        val currentUser = auth.currentUser ?: return null
        val uid = currentUser.uid

        val db = Firebase.firestore
        val snapshot = db.collection(USER_COLLECTION).document(uid).get()

        // Extract Firestore fields if present
        val nameFromDb: String? = try {
            snapshot.get(USER_NAME_PARAM) as? String
        } catch (_: Throwable) { null }

        val isLocalFromDb: Boolean? = try {
            snapshot.get(USER_IS_LOCAL_PARAM) as? Boolean
        } catch (_: Throwable) { null }

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
        val db = Firebase.firestore
        val userIds = users.map { it.id }

        val groupRef = db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_GROUPS_SUB_COLLECTION)
            .document(groupId)

        val currentUsers = try {
            @Suppress("UNCHECKED_CAST")
            (groupRef.get().get(EVENT_GROUPS_USERS_LIST) as? List<String>) ?: emptyList()
        } catch (_: Throwable) { emptyList() }

        // Merge and avoid duplicates
        val updated = (currentUsers + userIds).distinct()
        groupRef.set(
            mapOf(
                EVENT_GROUPS_USERS_LIST to updated,
                EVENT_GROUPS_USERS_COUNT to updated.size
            ),
            merge = true
        )
    }

    /** Convenience function to add a single user to a group. */
    suspend fun addUserToGroup(eventId: String, groupId: String, user: User) {
        addUsersToGroup(eventId, groupId, listOf(user))
    }

    // Add list of Events to Firebase Database
    suspend fun addEvents(eventList: List<Event>) {
        val db = Firebase.firestore

        for (event in eventList) {
            db.collection(EVENT_COLLECTION).add(event)
        }
    }

    // return all events in the database
    suspend fun getAllEvents(): List<Event> {
        val db = Firebase.firestore

        val collection = db.collection(EVENT_COLLECTION).get()
        val eventList = collection.documents.map { doc ->

            Event(
                id = doc.id,
                title = doc.get<String>(EVENT_NAME_PARAM),
                location = doc.get<String>(EVENT_LOCATION_PARAM),
                imageUrl = doc.get<String>(EVENT_IMAGE_URL_PARAM),
                fullnessPercentage = doc.get<Int>(EVENT_FULLNESS_PERCENTAGE_PARAM),
                isTrending = doc.get<Boolean>(EVENT_IS_TRENDING_PARAM)
            )
        }

        return eventList
    }

    /**
     * Add a user id into the event's individuals sub-collection.
     * Writes events/{eventId}/individuals/{userId} with minimal payload.
     */
    suspend fun addUserToEventIndividuals(eventId: String, userId: String) {
        val db = Firebase.firestore
        db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_INDIVIDUALS_SUB_COLLECTION)
            .document(userId)
            .set(mapOf(USER_UID_PARAM to userId), merge = true)
    }

    /**
     * Add the user to any available group (users list size < maxSize). If none exists, a new
     * group is created. The group document maintains a simple list of user ids under "users".
     * Returns the groupId the user has been added to.
     */
    suspend fun addUserToAvailableGroup(eventId: String, userId: String, maxSize: Int = 5): String {
        val db = Firebase.firestore
        val groupsColl = db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_GROUPS_SUB_COLLECTION)

        // Try to find an existing group with space via query (do NOT fetch all groups)
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
            // If user already present, return immediately
            if (users.contains(userId)) {
                return doc.id
            }
            targetGroupRef = doc.reference
        }

        // If no existing group found, create a new one
        if (targetGroupRef == null) {
            val newDocRef = groupsColl.add(
                mapOf(
                    EVENT_GROUPS_USERS_LIST to listOf<String>(),
                    EVENT_GROUPS_USERS_COUNT to 0
                )
            )
            targetGroupRef = newDocRef
        }

        // Append the user id if not present
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

    /**
     * Add the userId to the specified group users list (stored as List<String> of UIDs).
     * Creates the group document if it doesn't exist yet.
     */
    suspend fun addUserIdToGroup(eventId: String, groupId: String, userId: String) {
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
    }
}
