package hackatum.munichpulse.mvp.data.remote

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

/**
 * Interface for interacting with Firebase services.
 * Handles user authentication and other remote operations.
 */
class FirebaseInterface {
    var INSTANCE: FirebaseInterface = FirebaseInterface()

    /**
     * Signs in a user with email and password.
     * @param email The user's email.
     * @param password The user's password.
     */
    suspend fun userSignIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
    }
}