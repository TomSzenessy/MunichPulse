package hackatum.munichpulse.mvp.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.ui.map.MapController
import hackatum.munichpulse.mvp.ui.map.MapRenderer
import hackatum.munichpulse.mvp.ui.map.WebMapRenderer
import hackatum.munichpulse.mvp.viewmodel.MapEvent
import kotlin.js.json

// External declarations for Mapbox GL JS are in MapboxWrappers.kt

private external val process: dynamic

class JsMapController : MapController {
    var map: MapboxGl.Map? = null

    override fun zoomIn() {
        map?.let { 
            it.flyTo(json("zoom" to (it.getZoom() + 1)))
        }
    }

    override fun zoomOut() {
        map?.let {
            it.flyTo(json("zoom" to (it.getZoom() - 1)))
        }
    }

    override fun recenter(location: Location) {
        map?.flyTo(json(
            "center" to arrayOf(location.longitude, location.latitude),
            "zoom" to 14
        ))
    }

    override fun moveTo(location: Location, zoom: Double) {
        map?.flyTo(json(
            "center" to arrayOf(location.longitude, location.latitude),
            "zoom" to zoom
        ))
    }
}

@Composable
actual fun rememberMapController(): MapController {
    return remember { JsMapController() }
}

@Composable
actual fun MapRenderer(): MapRenderer {
    return remember { WebMapRenderer() }
}


