package hackatum.munichpulse.mvp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import hackatum.munichpulse.mvp.backend.FirebaseInterface
import kotlinx.browser.document
import org.jetbrains.skiko.wasm.onWasmReady
import org.w3c.dom.HTMLElement
import kotlin.js.JsModule
import kotlin.js.JsNonModule

@JsModule("firebase/app")
@JsNonModule
external object FirebaseAppModule {
    fun initializeApp(options: dynamic): dynamic
}

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

    // Initialize Firebase
    try {
        val options = js("""{
            apiKey: "AIzaSyD9EqFdZYATTdkY41UQbJwvCrTsBYASIv4",
            authDomain: "munichpulse-53738.firebaseapp.com",
            projectId: "munichpulse-53738",
            storageBucket: "munichpulse-53738.firebasestorage.app",
            messagingSenderId: "2145662120",
            appId: "1:2145662120:web:63736dabdff86137ee7e9d",
            measurementId: "G-3RYFMJ8MG3"
        }""")
        FirebaseAppModule.initializeApp(options)
        FirebaseInterface.initializeForWeb()
        console.log("Firebase initialized successfully")
    } catch (e: Throwable) {
        console.error("Failed to initialize Firebase", e)
    }

    // Initialize Persistence
    // SettingsRepository.init(settings) // TODO: Fix SettingsRepository

    onWasmReady {
        ComposeViewport(document.body!!) {
            App()
        }
    }
}
