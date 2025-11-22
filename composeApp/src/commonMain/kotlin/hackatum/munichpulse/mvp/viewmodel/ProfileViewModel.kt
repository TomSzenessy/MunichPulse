package hackatum.munichpulse.mvp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackatum.munichpulse.mvp.data.model.LogbookEntry
import hackatum.munichpulse.mvp.data.model.User
import hackatum.munichpulse.mvp.data.repository.MockUserRepository
import hackatum.munichpulse.mvp.data.repository.SettingsRepository
import hackatum.munichpulse.mvp.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Profile screen.
 * Manages user data, logbook entries, and application settings.
 * @property userRepository The repository to fetch user data from.
 */
class ProfileViewModel(
    private val userRepository: UserRepository = MockUserRepository()
) : ViewModel() {

    /**
     * A flow of the current user.
     */
    val user: StateFlow<User?> = userRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * A flow of logbook entries for the user.
     */
    val logbookEntries: StateFlow<List<LogbookEntry>> = userRepository.getLogbookEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
