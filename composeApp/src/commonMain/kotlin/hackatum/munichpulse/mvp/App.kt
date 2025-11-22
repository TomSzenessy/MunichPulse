package hackatum.munichpulse.mvp

import androidx.compose.runtime.*
import hackatum.munichpulse.mvp.ui.MainScreen
import hackatum.munichpulse.mvp.ui.UrbanPulseTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue

@Composable
@Preview
fun App() {
    UrbanPulseTheme {
        val showLogin by ViewController.getInstance().showLogInScreen().collectAsState()
        val isLoggedIn = ViewController.getInstance().isLoggedIn()

        if (isLoggedIn) ViewController.getInstance().closeSignInScreen()

        if (showLogin) {
            hackatum.munichpulse.mvp.ui.LoginScreen(
                onLoginSuccess = { name, isLocal ->
                    ViewController.getInstance().closeSignInScreen()
                }
            )
        } else {
            MainScreen()
        }
    }
}
