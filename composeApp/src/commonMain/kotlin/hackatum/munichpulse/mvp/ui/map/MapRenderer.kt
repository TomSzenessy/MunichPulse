package hackatum.munichpulse.mvp.ui.map

import androidx.compose.runtime.Composable
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.viewmodel.MapEvent

/**
 * Platform-agnostic map renderer interface
 */
interface MapRenderer {
    /**
     * Renders the map with the given state
     */
    @Composable
    fun renderMap(
        state: MapState,
        onMarkerClick: (String) -> Unit,
        modifier: androidx.compose.ui.Modifier
    )
}

/**
 * Immutable state representing the map and its markers
 */
data class MapState(
    val userLocation: Location? = null,
    val otherPeople: List<Location> = emptyList(),
    val events: List<MapEvent> = emptyList(),
    val selectedFilter: String = "All",
    val cameraPosition: CameraPosition? = null
)

/**
 * Camera position for the map
 */
data class CameraPosition(
    val center: Location,
    val zoom: Double = 12.0
)

/**
 * Map controller interface for platform-specific implementations
 */
interface MapController {
    fun zoomIn()
    fun zoomOut()
    fun recenter(location: Location)
    fun moveTo(location: Location, zoom: Double = 14.0)
}