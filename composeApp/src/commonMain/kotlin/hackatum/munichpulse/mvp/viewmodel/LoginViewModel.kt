package hackatum.munichpulse.mvp.viewmodel

import androidx.lifecycle.ViewModel
import hackatum.munichpulse.mvp.ViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Login screen.
 * Handles user login logic and manages UI state for the login process.
 */
class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    /**
     * The current UI state of the login screen.
     */
    val uiState = _uiState.asStateFlow()

    /**
     * Updates the user's name.
     * @param name The new name.
     */
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    /**
     * Toggles the user type between Local and Newby.
     * @param isLocal True if the user is a local, false otherwise.
     */
    fun toggleLocal(isLocal: Boolean) {
        _uiState.update { it.copy(isLocal = isLocal) }
    }

    /**
     * Sets the loading state.
     * @param isLoading True if a background operation is in progress.
     */
    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    /**
     * Initiates the login process.
     * @param name The user's name (if applicable).
     * @param isLocal Whether the user is a local.
     * @param onResult Callback invoked with the result of the login attempt.
     */
    fun login(name: String, isLocal: Boolean, onResult: (Boolean) -> Unit) {
        setLoading(true)
        CoroutineScope(Dispatchers.Default).launch {
            logIn(name, isLocal)
            onResult(true)
            ViewController.getInstance().closeSignInScreen()
            setLoading(false)
        }
    }
}

fun logIn(name: String, isLocal: Boolean) {
    ViewController.getInstance().logIn(name, isLocal)
}

data class LoginUiState(
    val name: String = "",
    val isLocal: Boolean = false,
    val isLoading: Boolean = false
)
