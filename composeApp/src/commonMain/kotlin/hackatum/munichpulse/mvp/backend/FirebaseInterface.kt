package hackatum.munichpulse.mvp.backend

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class FirebaseInterface {
    var INSTANCE: FirebaseInterface = FirebaseInterface()

    suspend fun userSignIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
    }
}