package hackatum.munichpulse.mvp.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.viewmodel.MapEvent
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.js.json
import hackatum.munichpulse.mvp.js.Secrets

// External declarations for Mapbox GL JS
@JsModule("mapbox-gl")
@JsNonModule
external object MapboxGl {
    var accessToken: String
    class Map(options: dynamic) {
        fun on(event: String, callback: (dynamic) -> Unit)
        fun remove()
        fun flyTo(options: dynamic)
        fun resize()
        fun getZoom(): Double
    }
    class Marker(options: dynamic) {
        fun setLngLat(lngLat: Array<Double>): Marker
        fun addTo(map: Map): Marker
        fun remove()
        fun getElement(): HTMLElement
    }
}

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
    otherPeople: List<Location>,
    events: List<MapEvent>,
    selectedFilter: String,
    onNavigateToEvent: (String) -> Unit
) {
    val jsController = mapController as? JsMapController
    
    // Container for the map
    val containerId = remember { "mapbox-container-${kotlin.random.Random.nextInt()}" }
    
    // We use a ref to hold the map instance to pass to LaunchedEffects
    val mapInstance = remember { mutableStateOf<MapboxGl.Map?>(null) }

    DisposableEffect(Unit) {
        val mapContainer = document.createElement("div") as HTMLDivElement
        mapContainer.id = containerId
        mapContainer.style.apply {
            position = "absolute"
            top = "0"
            left = "0"
            width = "100%"
            height = "100%"
            zIndex = "0" // Behind the canvas
            backgroundColor = "transparent"
        }
        console.log("Map container created with zIndex 0")
        document.body?.appendChild(mapContainer)

        MapboxGl.accessToken = Secrets.MAPBOX_PUBLIC_TOKEN

        val map = MapboxGl.Map(json(
            "container" to containerId,
            "style" to "mapbox://styles/mapbox/streets-v12",
            "center" to arrayOf(userLocation.longitude, userLocation.latitude),
            "zoom" to 12
        ))

        jsController?.map = map
        mapInstance.value = map

        map.on("load") {
            map.resize()
        }

        onDispose {
            map.remove()
            mapContainer.remove()
            jsController?.map = null
            mapInstance.value = null
        }
    }
    
    // Update User Location Marker (Blue Dot)
    DisposableEffect(mapInstance.value, userLocation) {
        val map = mapInstance.value
        if (map == null) return@DisposableEffect onDispose {}

        val element = document.createElement("div") as HTMLDivElement
        element.style.apply {
            width = "20px"
            height = "20px"
            backgroundColor = "#4285F4" // Blue
            borderRadius = "50%"
            border = "3px solid white"
            boxShadow = "0 0 5px rgba(0,0,0,0.3)"
        }
        
        val marker = MapboxGl.Marker(json("element" to element))
            .setLngLat(arrayOf(userLocation.longitude, userLocation.latitude))
            .addTo(map)

        onDispose {
            marker.remove()
        }
    }

    // Update Other People Markers (Green Dots)
    DisposableEffect(mapInstance.value, otherPeople) {
        val map = mapInstance.value
        if (map == null) return@DisposableEffect onDispose {}

        val markers = mutableListOf<MapboxGl.Marker>()
        
        otherPeople.forEach { personLocation ->
            val element = document.createElement("div") as HTMLDivElement
            element.style.apply {
                width = "16px"
                height = "16px"
                backgroundColor = "#0F9D58" // Green
                borderRadius = "50%"
                border = "2px solid white"
                boxShadow = "0 0 3px rgba(0,0,0,0.3)"
            }
            
            val marker = MapboxGl.Marker(json("element" to element))
                .setLngLat(arrayOf(personLocation.longitude, personLocation.latitude))
                .addTo(map)
            
            markers.add(marker)
        }

        onDispose {
            markers.forEach { it.remove() }
        }
    }

    // Update Event Markers (Red Pins)
    DisposableEffect(mapInstance.value, events, selectedFilter) {
        val map = mapInstance.value
        if (map == null) return@DisposableEffect onDispose {}

        val markers = mutableListOf<MapboxGl.Marker>()
        
        events.filter { selectedFilter == "All" || it.type == selectedFilter }.forEach { event ->
            val element = document.createElement("div") as HTMLDivElement
            element.style.apply {
                width = "24px"
                height = "24px"
                backgroundColor = "#EA4335" // Red
                borderRadius = "50% 50% 50% 0"
                border = "2px solid white"
                cursor = "pointer"
                boxShadow = "0 0 3px rgba(0,0,0,0.3)"
                transform = "rotate(-45deg)"
                marginTop = "-12px" // Adjust for pin point
            }
            
            element.onclick = {
                onNavigateToEvent(event.id)
                Unit
            }
            
            val marker = MapboxGl.Marker(json("element" to element))
                .setLngLat(arrayOf(event.location.longitude, event.location.latitude))
                .addTo(map)
            
            markers.add(marker)
        }

        onDispose {
            markers.forEach { it.remove() }
        }
    }
}
