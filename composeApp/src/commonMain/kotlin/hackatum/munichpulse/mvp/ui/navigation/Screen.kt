package hackatum.munichpulse.mvp.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Main : Screen("main")
    data object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
}
