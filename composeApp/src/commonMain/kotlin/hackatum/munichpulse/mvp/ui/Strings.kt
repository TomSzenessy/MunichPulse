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
    val groupsTab: String,
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
    val languageButton: String,
    val joinGroup: String,
    val leaveGroup: String,
    val openGroup: String,
    val confirmLeaveGroupTitle: String,
    val confirmLeaveGroupText: String,
    val cancel: String,
    val confirm: String
)

val EnStrings = AppStrings(
    appName = "Munich Pulse",
    slogan = "Find your beat in the city.",
    loginNameLabel = "What should we call you?",
    loginLocalLabel = "I am a Area Expert",
    loginNewbyLabel = "Explorer",
    loginLocalSubLabel = "I know my way around Munich",
    loginButton = "Start Exploring",
    loginGoogle = "Login with Google",
    loginGuest = "Continue as Guest",
    orDivider = "or",
    homeTab = "Home",
    groupsTab = "Groups",
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
    languageButton = "EN",
    joinGroup = "Join Group",
    leaveGroup = "Leave Group",
    openGroup = "Open Group",
    confirmLeaveGroupTitle = "Leave Group?",
    confirmLeaveGroupText = "Are you sure you want to leave this group?",
    cancel = "Cancel",
    confirm = "Confirm"
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
    groupsTab = "Gruppen",
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
    languageButton = "DE",
    joinGroup = "Gruppe beitreten",
    leaveGroup = "Gruppe verlassen",
    openGroup = "Gruppe öffnen",
    confirmLeaveGroupTitle = "Gruppe verlassen?",
    confirmLeaveGroupText = "Bist du sicher, dass du diese Gruppe verlassen möchtest?",
    cancel = "Abbrechen",
    confirm = "Bestätigen"
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
