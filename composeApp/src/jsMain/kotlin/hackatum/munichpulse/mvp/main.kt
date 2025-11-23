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

    val canvasStyle = document.createElement("style")
    canvasStyle.innerHTML = """
        html, body {
            margin: 0;
            padding: 0;
            overflow: hidden;
            background-color: transparent !important;
        }
        
        /* The Compose Canvas */
        canvas {
            position: absolute;
            top: 0;
            left: 0;
            z-index: 1;
            background-color: transparent !important;
            pointer-events: auto; /* Allow clicks on UI elements */
        }
        
        /* The Map Container */
        .mapbox-container {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 0; /* Changed from -1 to 0 to be below canvas but visible */
        }
        
        /* Mapbox map canvas specifically */
        .mapboxgl-canvas {
            z-index: 0;
        }
        
        /* Make sure all UI elements are clickable */
        .compose-ui-layer {
            pointer-events: auto;
            z-index: 2;
        }
    """.trimIndent()
    document.head?.appendChild(canvasStyle)

    onWasmReady {
        ComposeViewport(document.body!!) {
            App()
        }
    }
}
