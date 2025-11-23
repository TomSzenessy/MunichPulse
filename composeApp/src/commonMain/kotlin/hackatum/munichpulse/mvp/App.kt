package hackatum.munichpulse.mvp

import LoadEventMockData
import LoadEventMockData.Companion.loadEventsFromResources
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import hackatum.munichpulse.mvp.backend.FirebaseInterface
import hackatum.munichpulse.mvp.data.model.Event
import hackatum.munichpulse.mvp.data.repository.GroupRepository
import hackatum.munichpulse.mvp.ui.EventDetailScreen
import hackatum.munichpulse.mvp.ui.LoginScreen
import hackatum.munichpulse.mvp.ui.MainScreen
import hackatum.munichpulse.mvp.ui.ProvideAppStrings
import hackatum.munichpulse.mvp.ui.SplashScreen
import hackatum.munichpulse.mvp.ui.navigation.Screen
import hackatum.munichpulse.mvp.ui.theme.UrbanPulseTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

import hackatum.munichpulse.mvp.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun App() {
    val isDarkMode by SettingsRepository.isDarkMode.collectAsState()

//    CoroutineScope(Dispatchers.Unconfined).launch {
//        val events : List<Event> = loadEventsFromResources("files/mock_events_two.json")
//
//        FirebaseInterface.getInstance().addEvents(events)
//        print("Baum")
//        println("XKCD" + FirebaseInterface.getInstance().getAllEvents())
//    }

    LaunchedEffect(Unit) {
        // Initialize repositories
        GroupRepository.init()
    }

    ProvideAppStrings {
        UrbanPulseTheme(useDarkTheme = isDarkMode) {
            val navController = rememberNavController()
            
            NavHost(navController = navController, startDestination = Screen.Splash.route) {
                composable(Screen.Splash.route) {
                    SplashScreen(
                        onSplashFinished = {
                            if (ViewController.isLoggedIn()) {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }
                
                composable(Screen.Login.route) {
                    LoginScreen(onLoginSuccess = { name, isLocal ->
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    })
                }
                
                composable(Screen.Main.route) {
                    MainScreen(
                        onEventClick = { eventId ->
                            navController.navigate(Screen.EventDetail.createRoute(eventId))
                        }
                    )
                }
                
                composable(
                    route = Screen.EventDetail.route,
                    arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
                    EventDetailScreen(
                        eventId = eventId,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
