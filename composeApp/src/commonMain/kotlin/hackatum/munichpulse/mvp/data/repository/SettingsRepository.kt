
package hackatum.munichpulse.mvp.data.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing application settings.
 * Handles persistence of dark mode and language preferences.
 */
object SettingsRepository {
    // Simple in-memory settings implementation to avoid dependency issues
    private val settings: Settings = object : Settings {
        private val map = mutableMapOf<String, Any>()
        override val keys: Set<String> get() = map.keys
        override val size: Int get() = map.size
        override fun clear() = map.clear()
        override fun getBoolean(key: String, defaultValue: Boolean): Boolean = map[key] as? Boolean ?: defaultValue
        override fun getBooleanOrNull(key: String): Boolean? = map[key] as? Boolean
        override fun getDouble(key: String, defaultValue: Double): Double = map[key] as? Double ?: defaultValue
        override fun getDoubleOrNull(key: String): Double? = map[key] as? Double
        override fun getFloat(key: String, defaultValue: Float): Float = map[key] as? Float ?: defaultValue
        override fun getFloatOrNull(key: String): Float? = map[key] as? Float
        override fun getInt(key: String, defaultValue: Int): Int = map[key] as? Int ?: defaultValue
        override fun getIntOrNull(key: String): Int? = map[key] as? Int
        override fun getLong(key: String, defaultValue: Long): Long = map[key] as? Long ?: defaultValue
        override fun getLongOrNull(key: String): Long? = map[key] as? Long
        override fun getString(key: String, defaultValue: String): String = map[key] as? String ?: defaultValue
        override fun getStringOrNull(key: String): String? = map[key] as? String
        override fun hasKey(key: String): Boolean = map.containsKey(key)
        override fun putBoolean(key: String, value: Boolean) { map[key] = value }
        override fun putDouble(key: String, value: Double) { map[key] = value }
        override fun putFloat(key: String, value: Float) { map[key] = value }
        override fun putInt(key: String, value: Int) { map[key] = value }
        override fun putLong(key: String, value: Long) { map[key] = value }
        override fun putString(key: String, value: String) { map[key] = value }
        override fun remove(key: String) { map.remove(key) }
    }
    
    private val _isDarkMode = MutableStateFlow(settings.getBoolean("isDarkMode", true))
    /**
     * A flow indicating whether dark mode is enabled.
     */
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow(settings.getString("language", "English"))
    /**
     * A flow of the currently selected language.
     */
    val language = _language.asStateFlow()

    /**
     * Sets the dark mode preference.
     * @param enabled True to enable dark mode, false to disable.
     */
    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        settings.putBoolean("isDarkMode", enabled)
    }

    /**
     * Sets the language preference.
     * @param language The language code or name.
     */
    fun setLanguage(lang: String) {
        _language.value = lang
        settings.putString("language", lang)
    }
}
