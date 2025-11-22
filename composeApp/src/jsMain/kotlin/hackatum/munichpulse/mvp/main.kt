package hackatum.munichpulse.mvp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import hackatum.munichpulse.mvp.data.db.getDatabase
import hackatum.munichpulse.mvp.data.db.JsPlatformContext

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize Persistence
    // SettingsRepository.init(settings) // TODO: Fix SettingsRepository

    val database = getDatabase(JsPlatformContext)

    ComposeViewport(viewportContainerId = "composeApplication") {
        App(database)
    }
}
