package hackatum.munichpulse.mvp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackatum.munichpulse.mvp.backend.FirebaseInterface
import hackatum.munichpulse.mvp.data.model.User
import hackatum.munichpulse.mvp.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Profile screen.
 * Loads actual user data from Firebase and exposes application settings.
 */
class ProfileViewModel : ViewModel() {

    /**
     * A flow of the current user.
     */
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        // Load the currently signed-in user's data once on initialization
        viewModelScope.launch {
            _user.value = FirebaseInterface.getCurrentUserData()
        }
    }

    /**
     * A flow indicating whether dark mode is enabled.
     */
    val isDarkMode: StateFlow<Boolean> = SettingsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    /**
     * A flow of the currently selected language.
     */
    val language: StateFlow<String> = SettingsRepository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "English")

    /**
     * Enables or disables dark mode.
     * @param enabled True to enable dark mode, false to disable.
     */
    fun setDarkMode(enabled: Boolean) {
        SettingsRepository.setDarkMode(enabled)
    }

    /**
     * Sets the application language.
     * @param language The language code or name (e.g., "English", "German").
     */
    fun setLanguage(language: String) {
        SettingsRepository.setLanguage(language)
    }
}
