package hackatum.munichpulse.mvp.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import hackatum.munichpulse.mvp.data.repository.SettingsRepository

data class AppStrings(
    val appName: String,
    val slogan: String,
    val loginNameLabel: String,
    val loginLocalLabel: String,
    val loginNewbyLabel: String,
    val loginLocalSubLabel: String,
    val loginButton: String,
    val loginGoogle: String,
    val loginGuest: String,
    val orDivider: String,
    val homeTab: String,
    val squadsTab: String,
    val mapTab: String,
    val profileTab: String,
    val settingsTitle: String,
    val darkModeLabel: String,
    val languageLabel: String,
    val logbookTitle: String,
    val searchPlaceholder: String,
    val trendingHeader: String,
    val nearbyHeader: String,
    val discoverHeader: String,
    val languageButton: String
)

val EnStrings = AppStrings(
    appName = "MunichPulse",
    slogan = "Find your beat in the city.",
    loginNameLabel = "What should we call you?",
    loginLocalLabel = "I am a Local",
    loginNewbyLabel = "Newby",
    loginLocalSubLabel = "I know my way around Munich",
    loginButton = "Start Exploring",
    loginGoogle = "Login with Google",
    loginGuest = "Continue as Guest",
    orDivider = "or",
    homeTab = "Home",
    squadsTab = "Squads",
    mapTab = "Map",
    profileTab = "Profile",
    settingsTitle = "Settings",
    darkModeLabel = "Dark Mode",
    languageLabel = "Language",
    logbookTitle = "Logbook",
    searchPlaceholder = "Search for events or places",
    trendingHeader = "Trending Now",
    nearbyHeader = "Near You",
    discoverHeader = "Discover New Events",
    languageButton = "EN"
)

val DeStrings = AppStrings(
    appName = "MunichPulse",
    slogan = "Finde deinen Rhythmus in der Stadt.",
    loginNameLabel = "Wie sollen wir dich nennen?",
    loginLocalLabel = "Ich bin ein Local",
    loginNewbyLabel = "Neu hier",
    loginLocalSubLabel = "Ich kenne mich in München aus",
    loginButton = "Erkunden starten",
    loginGoogle = "Mit Google anmelden",
    loginGuest = "Als Gast fortfahren",
    orDivider = "oder",
    homeTab = "Start",
    squadsTab = "Squads",
    mapTab = "Karte",
    profileTab = "Profil",
    settingsTitle = "Einstellungen",
    darkModeLabel = "Dunkelmodus",
    languageLabel = "Sprache",
    logbookTitle = "Logbuch",
    searchPlaceholder = "Suche nach Events oder Orten",
    trendingHeader = "Jetzt angesagt",
    nearbyHeader = "In deiner Nähe",
    discoverHeader = "Neue Events entdecken",
    languageButton = "DE"
)

val LocalAppStrings = staticCompositionLocalOf { EnStrings }

@Composable
fun ProvideAppStrings(
    content: @Composable () -> Unit
) {
    val language by SettingsRepository.language.collectAsState()
    val strings = if (language == "German") DeStrings else EnStrings
    
    CompositionLocalProvider(LocalAppStrings provides strings) {
        content()
    }
}
