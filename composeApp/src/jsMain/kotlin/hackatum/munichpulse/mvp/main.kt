package hackatum.munichpulse.mvp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Inject Mapbox scripts
    val script = document.createElement("script") as HTMLElement
    script.setAttribute("src", "https://api.mapbox.com/mapbox-gl-js/v3.1.2/mapbox-gl.js")
    document.head?.appendChild(script)

    val link = document.createElement("link") as HTMLElement
    link.setAttribute("href", "https://api.mapbox.com/mapbox-gl-js/v3.1.2/mapbox-gl.css")
    link.setAttribute("rel", "stylesheet")
    document.head?.appendChild(link)

    // Initialize Persistence
    // SettingsRepository.init(settings) // TODO: Fix SettingsRepository

    ComposeViewport(document.body!!) {
        App()
    }
}
