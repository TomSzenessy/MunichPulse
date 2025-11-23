package hackatum.munichpulse.mvp.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Main : Screen("main?screen={screen}&groupId={groupId}") {
        fun createRoute(screen: String? = null, groupId: String? = null): String {
            val screenPart = if (screen != null) "screen=$screen" else ""
            val groupPart = if (groupId != null) "groupId=$groupId" else ""
            val params = listOf(screenPart, groupPart).filter { it.isNotEmpty() }.joinToString("&")
            return if (params.isNotEmpty()) "main?$params" else "main"
        }
    }
    data object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
}
