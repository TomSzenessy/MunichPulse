package hackatum.munichpulse.mvp

import hackatum.munichpulse.mvp.backend.FirebaseInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ViewController {

    // Start with the Login screen visible until user signs in
    private val _showSignInScreen = MutableStateFlow(true)
    private val showSignInScreen: StateFlow<Boolean> = _showSignInScreen.asStateFlow()

    fun showLogInScreen(): StateFlow<Boolean> {
        return showSignInScreen
    }

    fun closeSignInScreen() {
        _showSignInScreen.value = false
    }

    fun isLoggedIn(): Boolean {
        return FirebaseInterface.getInstance().isSignedIn()
    }

    fun logIn(name: String, isLocal: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            FirebaseInterface.getInstance().userSignIn(name, isLocal)
        }
    }

    // Sign in directly with a Google ID token (e.g., on a fresh install or another device)
    fun signInWithGoogle(idToken: String, name: String? = null, isLocal: Boolean? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            FirebaseInterface.getInstance().signInWithGoogle(idToken, name, isLocal)
        }
    }

    // Link the currently signed-in (likely anonymous) account with Google to upgrade it
    fun linkWithGoogle(idToken: String, name: String? = null, isLocal: Boolean? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            FirebaseInterface.getInstance().linkWithGoogle(idToken, name, isLocal)
        }
    }

    // Sign in with email and password (works on Android and Web via dev.gitlive)
    fun signInWithEmailPassword(email: String, password: String, name: String, isLocal: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            FirebaseInterface.getInstance().signInWithEmailPassword(email, password, name, isLocal)
        }
    }
}
