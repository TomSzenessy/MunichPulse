package hackatum.munichpulse.mvp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.viewmodel.MapEvent

interface MapController {
    fun zoomIn()
    fun zoomOut()
    fun recenter(location: Location)
}

@Composable
expect fun rememberMapController(): MapController

@Composable
expect fun MapComponent(
    modifier: Modifier = Modifier,
    mapController: MapController,
    userLocation: Location,
    events: List<MapEvent>,
    selectedFilter: String,
    onNavigateToEvent: (String) -> Unit
)
