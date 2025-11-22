package hackatum.munichpulse.mvp.backend

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import hackatum.munichpulse.mvp.ViewController


const val USER_COLLECTION: String = "users"
const val USER_UID_PARAM: String = "uid"
const val USER_IS_LOCAL_PARAM: String = "isLocal"
const val USER_NAME_PARAM: String = "name"

const val EMULATOR_IP: String = "131.159.207.110"



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
        createUser(name, isLocal)
    }

    suspend fun createUser(name: String, isLocal: Boolean) {
        val auth = Firebase.auth

        // Ensure we have an authenticated user context
        var currentUser = auth.currentUser

        // If still no user, we cannot proceed
        val uid = currentUser?.uid ?: return

        val userData = buildMap<String, Any> {
            put(USER_UID_PARAM, uid)
            put(USER_IS_LOCAL_PARAM, isLocal)
            put(USER_NAME_PARAM, name)
        }

        // Upsert user document so it works for anonymous and Google-authenticated users
        val db = Firebase.firestore
        db.collection(USER_COLLECTION).document(uid).set(userData, merge = true)
    }
}
