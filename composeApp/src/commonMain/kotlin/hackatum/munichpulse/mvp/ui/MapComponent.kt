package hackatum.munichpulse.mvp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.ui.map.MapController
import hackatum.munichpulse.mvp.ui.map.MapRenderer
import hackatum.munichpulse.mvp.ui.map.MapState
import hackatum.munichpulse.mvp.viewmodel.MapEvent

@Composable
expect fun rememberMapController(): MapController

@Composable
expect fun MapRenderer(): MapRenderer

@Composable
fun MapComponent(
    modifier: Modifier = Modifier,
    mapController: MapController,
    userLocation: Location,
    otherPeople: List<Location>,
    events: List<MapEvent>,
    selectedFilter: String,
    onNavigateToEvent: (String) -> Unit
) {
    val renderer = MapRenderer()
    
    val mapState = MapState(
        userLocation = userLocation,
        otherPeople = otherPeople,
        events = events,
        selectedFilter = selectedFilter
    )
    
    renderer.renderMap(
        state = mapState,
        mapController = mapController,
        onMarkerClick = onNavigateToEvent,
        modifier = modifier
    )
}
