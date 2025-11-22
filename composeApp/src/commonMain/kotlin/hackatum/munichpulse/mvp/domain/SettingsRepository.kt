package hackatum.munichpulse.mvp.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SettingsRepository {
    private val _isDarkMode = MutableStateFlow(true) // Default to Dark Mode
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow("English") // Default to English
    val language = _language.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }
}
