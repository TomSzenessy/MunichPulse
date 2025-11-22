package hackatum.munichpulse.mvp

import androidx.compose.runtime.*
import hackatum.munichpulse.mvp.data.repository.GroupRepository
import hackatum.munichpulse.mvp.ui.LoginScreen
import hackatum.munichpulse.mvp.ui.MainScreen
import hackatum.munichpulse.mvp.ui.ProvideAppStrings
import hackatum.munichpulse.mvp.ui.theme.UrbanPulseTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue

@Composable
fun App() {
    LaunchedEffect(Unit) {
        // Initialize repositories
        GroupRepository.init()
        // One-time check at app start: if already logged in, skip login screen
        if (ViewController.getInstance().isLoggedIn()) {
            ViewController.getInstance().closeSignInScreen()
        }
    }

    ProvideAppStrings {
        UrbanPulseTheme {
            val showLogin by ViewController.getInstance().showLogInScreen().collectAsState()

            if (showLogin) {
                LoginScreen(onLoginSuccess = { name, isLocal ->
                    ViewController.getInstance().closeSignInScreen()
                }
                )
            } else {
                MainScreen()
            }
        }
    }
}
