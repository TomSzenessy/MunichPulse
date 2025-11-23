package hackatum.munichpulse.mvp.ui.components
import hackatum.munichpulse.mvp.js.Secrets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.browser.document
import kotlinx.dom.createElement
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import org.w3c.dom.HTMLElement
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import kotlinx.browser.window
import kotlin.random.Random

@Composable
actual fun MapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Double
) {
    // Mapbox implementation for Web
    // We inject the Mapbox GL JS script and CSS if not already present
    // And then create a container div

    val mapboxAccessToken = Secrets.MAPBOX_PUBLIC_TOKEN

    // Since we are in Canvas based Compose, we can't easily render a DOM element *inside* the canvas layout.
    // However, we can create a floating div on top of the canvas.
    // For simplicity in this MVP, we will append a div to the body and position it absolutely to cover the screen
    // or a specific area. But positioning it relative to the Compose layout is hard.
    
    val mapContainerId = "map-container-${Random.nextInt()}"
   
    SideEffect {
        // Check if map container exists, if not create it
        if (document.getElementById(mapContainerId) == null) {
            val div = document.createElement("div") as HTMLElement
            div.id = mapContainerId
            div.style.position = "absolute"
            div.style.top = "0"
            div.style.left = "0"
            div.style.width = "100%"
            div.style.height = "100%"
            div.style.zIndex = "0" // Behind the canvas
            
            document.body?.appendChild(div)
            
            // Initialize Mapbox
            // Poll for mapboxgl to be ready
            val script = """
                function initMap() {
                    if (typeof mapboxgl !== 'undefined') {
                        mapboxgl.accessToken = '$mapboxAccessToken';
                        new mapboxgl.Map({
                            container: '$mapContainerId',
                            style: 'mapbox://styles/mapbox/dark-v11',
                            center: [$longitude, $latitude],
                            zoom: $zoom
                        });
                    } else {
                        setTimeout(initMap, 100);
                    }
                }
                initMap();
            """
            js("eval(script)")
        }
    }
    
    // We render a transparent box to take up space in Compose layout if needed, 
    // but here we are just injecting a full screen map background.
}
