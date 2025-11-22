package hackatum.munichpulse.mvp

import androidx.lifecycle.ViewModel
import hackatum.munichpulse.mvp.backend.FirebaseInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewController : ViewModel() {

    companion object {
        var INSTANCE: ViewController = ViewController()

        fun getInstance(): ViewController {
            return INSTANCE
        }
    }
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
}
