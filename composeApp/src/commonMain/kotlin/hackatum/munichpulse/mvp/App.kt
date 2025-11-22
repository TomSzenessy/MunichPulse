package hackatum.munichpulse.mvp

import androidx.compose.runtime.*
import hackatum.munichpulse.mvp.ui.MainScreen
import hackatum.munichpulse.mvp.ui.UrbanPulseTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    UrbanPulseTheme {
        var isLoggedIn by remember { mutableStateOf(false) }

        if (isLoggedIn) {
            MainScreen()
        } else {
            hackatum.munichpulse.mvp.ui.LoginScreen(
                onLoginSuccess = { name, isLocal -> 
                    // TODO: Save user session (name, isLocal)
                    isLoggedIn = true 
                }
            )
        }
    }
}