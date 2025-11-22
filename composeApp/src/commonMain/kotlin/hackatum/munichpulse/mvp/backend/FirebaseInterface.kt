package hackatum.munichpulse.mvp.backend

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.firestore.firestore
import hackatum.munichpulse.mvp.data.model.Event
import hackatum.munichpulse.mvp.data.model.User
import kotlin.String


const val USER_COLLECTION: String = "users"
const val USER_UID_PARAM: String = "uid"
const val USER_IS_LOCAL_PARAM: String = "isLocal"
const val USER_NAME_PARAM: String = "name"

const val EMULATOR_IP: String = "131.159.207.110"


const val EVENT_COLLECTION: String = "events"
const val EVENT_NAME_PARAM: String = "name"
const val EVENT_LOCATION_PARAM: String = "location"
const val EVENT_DATA_BEGIN_PARAM: String = "begin_date"
const val EVENT_GROUPS_SUB_COLLECTION: String = "groups"
const val EVENT_GROUPS_USERS_LIST: String = "users"
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
                Firebase.auth.useEmulator(EMULATOR_IP, 9099)
                Firebase.firestore.useEmulator(EMULATOR_IP, 8080)
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
        val userList = users.map { user ->
            mapOf(
                USER_UID_PARAM to user.id,
                USER_NAME_PARAM to user.name,
                USER_IS_LOCAL_PARAM to user.isLocal
            )
        }

        db.collection(EVENT_COLLECTION)
            .document(eventId)
            .collection(EVENT_GROUPS_SUB_COLLECTION)
            .document(groupId)
            // Merge so other fields on the group doc are preserved
            .set(mapOf(EVENT_GROUPS_USERS_LIST to userList), merge = true)
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
}
