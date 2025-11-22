package hackatum.munichpulse.mvp

import androidx.compose.runtime.*
import hackatum.munichpulse.mvp.data.db.MunichPulseDatabase
import hackatum.munichpulse.mvp.data.repository.GroupRepository
import hackatum.munichpulse.mvp.ui.LoginScreen
import hackatum.munichpulse.mvp.ui.MainScreen
import hackatum.munichpulse.mvp.ui.ProvideAppStrings
import hackatum.munichpulse.mvp.ui.theme.UrbanPulseTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue

@Composable
fun App(database: MunichPulseDatabase) {
    LaunchedEffect(Unit) {
        GroupRepository.init(database)
    }

    ProvideAppStrings {
        UrbanPulseTheme {
            var isLoggedIn by remember { mutableStateOf(false) }

            if (isLoggedIn) {
                MainScreen()
            } else {
                LoginScreen(
                    onLoginSuccess = { name, isLocal ->
                        // TODO: Save user session (name, isLocal)
                        isLoggedIn = true
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    ProvideAppStrings {
        UrbanPulseTheme {
            LoginScreen(onLoginSuccess = { _, _ -> })
        }
    }
}
