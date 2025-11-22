package hackatum.munichpulse.mvp.backend

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.firestore.firestore
import hackatum.munichpulse.mvp.data.model.User


const val USER_COLLECTION: String = "users"
const val USER_UID_PARAM: String = "uid"
const val USER_IS_LOCAL_PARAM: String = "isLocal"
const val USER_NAME_PARAM: String = "name"

const val EMULATOR_IP: String = "131.159.207.110"


const val EVENT_COLLECTION: String = "events"
const val EVENT_NAME_PARAM: String = "name"
const val EVENT_DATA_BEGIN_PARAM: String = "begin_date"
const val EVENT_TRACKS_SUB_COLLECTION: String = "tracks"
const val EVENT_TRACK_SUB_COLLECTION_POSITION: String = "gps_coords"



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

    fun joinEvent(eventId: String) {

    }
}
