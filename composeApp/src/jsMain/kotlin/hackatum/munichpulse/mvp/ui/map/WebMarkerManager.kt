package hackatum.munichpulse.mvp.ui.map

import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.viewmodel.MapEvent
import hackatum.munichpulse.mvp.ui.MapboxGl
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import kotlin.js.json

/**
 * Web implementation of MarkerManager using Mapbox GL JS
 */
class WebMarkerManager(private val map: MapboxGl.Map) : MarkerManager {
    private var userMarker: MapboxGl.Marker? = null
    private val peopleMarkers = mutableListOf<MapboxGl.Marker>()
    private val eventMarkers = mutableListOf<MapboxGl.Marker>()

    override fun updateUserLocation(location: Location) {
        userMarker?.remove()

        val element = createUserMarkerElement()

        userMarker = MapboxGl.Marker(json("element" to element))
            .setLngLat(arrayOf(location.longitude, location.latitude))
            .addTo(map)
    }

    override fun updateOtherPeople(people: List<Location>) {
        peopleMarkers.forEach { it.remove() }
        peopleMarkers.clear()

        people.forEach { personLocation ->
            val element = createPersonMarkerElement()

            val marker = MapboxGl.Marker(json("element" to element))
                .setLngLat(arrayOf(personLocation.longitude, personLocation.latitude))
                .addTo(map)
            
            peopleMarkers.add(marker)
        }
    }

    override fun updateEvents(
        events: List<MapEvent>,
        selectedFilter: String,
        onNavigateToEvent: (String) -> Unit
    ) {
        eventMarkers.forEach { it.remove() }
        eventMarkers.clear()

        events.filter { selectedFilter == "All" || it.type == selectedFilter }.forEach { event ->
            val element = createEventMarkerElement(event.id, onNavigateToEvent)

            val marker = MapboxGl.Marker(json("element" to element))
                .setLngLat(arrayOf(event.location.longitude, event.location.latitude))
                .addTo(map)
            
            eventMarkers.add(marker)
        }
    }

    override fun clearAll() {
        userMarker?.remove()
        peopleMarkers.forEach { it.remove() }
        eventMarkers.forEach { it.remove() }
        peopleMarkers.clear()
        eventMarkers.clear()
    }

    private fun createUserMarkerElement(): HTMLDivElement {
        val element = document.createElement("div") as HTMLDivElement
        element.style.apply {
            width = "${MarkerStyles.USER_MARKER_SIZE}px"
            height = "${MarkerStyles.USER_MARKER_SIZE}px"
            backgroundColor = MarkerStyles.USER_MARKER_COLOR
            borderRadius = "50%"
            border = "${MarkerStyles.BORDER_WIDTH}px solid ${MarkerStyles.BORDER_COLOR}"
            boxShadow = "0 0 5px rgba(0,0,0,0.3)"
        }
        return element
    }

    private fun createPersonMarkerElement(): HTMLDivElement {
        val element = document.createElement("div") as HTMLDivElement
        element.style.apply {
            width = "${MarkerStyles.PERSON_MARKER_SIZE}px"
            height = "${MarkerStyles.PERSON_MARKER_SIZE}px"
            backgroundColor = MarkerStyles.PERSON_MARKER_COLOR
            borderRadius = "50%"
            border = "2px solid ${MarkerStyles.BORDER_COLOR}"
            boxShadow = "0 0 3px rgba(0,0,0,0.3)"
        }
        return element
    }

    private fun createEventMarkerElement(
        eventId: String,
        onNavigateToEvent: (String) -> Unit
    ): HTMLDivElement {
        val element = document.createElement("div") as HTMLDivElement
        element.style.apply {
            width = "${MarkerStyles.EVENT_MARKER_SIZE}px"
            height = "${MarkerStyles.EVENT_MARKER_SIZE}px"
            backgroundColor = MarkerStyles.EVENT_MARKER_COLOR
            borderRadius = "50% 50% 50% 0"
            border = "2px solid ${MarkerStyles.BORDER_COLOR}"
            cursor = "pointer"
            boxShadow = "0 0 3px rgba(0,0,0,0.3)"
            transform = "rotate(-45deg)"
            marginTop = "-12px" // Adjust for pin point
        }

        element.onclick = {
            onNavigateToEvent(eventId)
            Unit
        }
        
        return element
    }
}