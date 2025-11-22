package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.viewmodel.MapEvent

class JsMapController : MapController {
    override fun zoomIn() {
        println("Zoom In")
    }

    override fun zoomOut() {
        println("Zoom Out")
    }

    override fun recenter(location: Location) {
        println("Recenter to $location")
    }
}

@Composable
actual fun rememberMapController(): MapController {
    return remember { JsMapController() }
}

@Composable
actual fun MapComponent(
    modifier: Modifier,
    mapController: MapController,
    userLocation: Location,
    events: List<MapEvent>,
    selectedFilter: String,
    onNavigateToEvent: (String) -> Unit
) {
    Box(
        modifier = modifier.background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text("Map Placeholder (JS)")
    }
}
