package hackatum.munichpulse.mvp.ui.map

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.ui.MapboxGl
import hackatum.munichpulse.mvp.viewmodel.MapEvent
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.js.json
import hackatum.munichpulse.mvp.js.Secrets

/**
 * Web implementation of MapRenderer using Mapbox GL JS with efficient diffing
 */
class WebMapRenderer : MapRenderer {
    
    @Composable
    override fun renderMap(
        state: MapState,
        mapController: MapController,
        onMarkerClick: (String) -> Unit,
        modifier: Modifier
    ) {
        val containerId = remember { "mapbox-container-${kotlin.random.Random.nextInt()}" }
        val mapInstance = remember { mutableStateOf<MapboxGl.Map?>(null) }
        
        // Link controller to map
        LaunchedEffect(mapInstance.value) {
            if (mapController is hackatum.munichpulse.mvp.ui.JsMapController) {
                mapController.map = mapInstance.value
            }
        }

        val markerManager = remember { mutableStateOf<WebMarkerManager?>(null) }
        val previousState = remember { mutableStateOf<MapState?>(null) }
        
        // State for layout coordinates
        var layoutCoordinates by remember { mutableStateOf<androidx.compose.ui.layout.LayoutCoordinates?>(null) }

        // Box to track position in Compose layout
        androidx.compose.foundation.layout.Box(
            modifier = modifier.onGloballyPositioned { 
                layoutCoordinates = it 
            }
        )

        DisposableEffect(Unit) {
            val mapContainer = document.createElement("div") as HTMLDivElement
            mapContainer.id = containerId
            mapContainer.className = "mapbox-container"
            mapContainer.style.apply {
                position = "absolute"
                zIndex = "-1" // Map below the UI canvas
                visibility = "hidden"
            }
            document.body?.appendChild(mapContainer)

            MapboxGl.accessToken = Secrets.MAPBOX_PUBLIC_TOKEN

            val initialCenter = state.userLocation?.let { 
                arrayOf(it.longitude, it.latitude) 
            } ?: arrayOf(11.5820, 48.1351) // Munich coordinates

            val map = MapboxGl.Map(json(
                "container" to containerId,
                "style" to "mapbox://styles/mapbox/dark-v11",
                "center" to initialCenter,
                "zoom" to 12,
                "attributionControl" to false
            ))

            mapInstance.value = map
            markerManager.value = WebMarkerManager(map)

            map.on("load") {
                map.resize()
                mapContainer.style.visibility = "visible"
            }

            onDispose {
                markerManager.value?.clearAll()
                map.remove()
                mapContainer.remove()
                mapInstance.value = null
                markerManager.value = null
                previousState.value = null
            }
        }
        
        // Update position and size of the map container to match the Compose Box
        LaunchedEffect(layoutCoordinates) {
            val coords = layoutCoordinates ?: return@LaunchedEffect
            val position = coords.positionInWindow()
            val size = coords.size
            
            val mapContainer = document.getElementById(containerId) as? HTMLElement
            mapContainer?.style?.apply {
                top = "${position.y}px"
                left = "${position.x}px"
                width = "${size.width}px"
                height = "${size.height}px"
            }
            mapInstance.value?.resize()
        }

        // Efficient marker updates using diffing
        LaunchedEffect(state) {
            val currentMap = mapInstance.value ?: return@LaunchedEffect
            val currentManager = markerManager.value ?: return@LaunchedEffect
            val prev = previousState.value

            if (prev != null) {
                // Calculate diff and apply changes efficiently
                val diff = MarkerDiffUtil.calculateDiff(prev, state)
                applyMarkerDiff(currentManager, diff, onMarkerClick)
            } else {
                // Initial load - set all markers
                currentManager.updateUserLocation(state.userLocation ?: return@LaunchedEffect)
                currentManager.updateOtherPeople(state.otherPeople)
                currentManager.updateEvents(state.events, state.selectedFilter, onMarkerClick)
            }

            previousState.value = state
        }

        // Handle camera position changes
        LaunchedEffect(state.cameraPosition) {
            state.cameraPosition?.let { camera ->
                mapInstance.value?.flyTo(json(
                    "center" to arrayOf(camera.center.longitude, camera.center.latitude),
                    "zoom" to camera.zoom
                ))
            }
        }
    }

    private fun applyMarkerDiff(
        markerManager: WebMarkerManager,
        diff: MarkerDiffResult,
        onMarkerClick: (String) -> Unit
    ) {
        // Apply user location changes
        when (diff.userLocationDiff) {
            is UserLocationDiff.ADDED, is UserLocationDiff.UPDATED -> {
                val location = when (diff.userLocationDiff) {
                    is UserLocationDiff.ADDED -> diff.userLocationDiff.location
                    is UserLocationDiff.UPDATED -> diff.userLocationDiff.location
                    else -> return
                }
                markerManager.updateUserLocation(location)
            }
            is UserLocationDiff.REMOVED -> {
                // Handle user location removal if needed
            }
            is UserLocationDiff.NONE -> { /* No change */ }
        }

        // Apply people marker changes
        if (diff.peopleDiff.added.isNotEmpty() || diff.peopleDiff.removed.isNotEmpty()) {
            // For simplicity, we update all people markers
            // In a more optimized version, we could add/remove individual markers
            markerManager.updateOtherPeople(diff.peopleDiff.added + diff.peopleDiff.unchanged)
        }

        // Apply event marker changes
        if (diff.eventsDiff.added.isNotEmpty() || diff.eventsDiff.removed.isNotEmpty() || diff.eventsDiff.updated.isNotEmpty()) {
            // For simplicity, we update all event markers
            // In a more optimized version, we could add/remove/update individual markers
            val allEvents = diff.eventsDiff.added + diff.eventsDiff.unchanged + diff.eventsDiff.updated.map { it.second }
            markerManager.updateEvents(allEvents, "All", onMarkerClick) // Use "All" filter since events are pre-filtered
        }
    }
}
