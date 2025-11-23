package hackatum.munichpulse.mvp.ui.map

import hackatum.munichpulse.mvp.model.Location

/**
 * Sealed class representing different types of map markers
 */
sealed class MapMarker {
    abstract val id: String
    abstract val location: Location
    
    data class UserMarker(
        override val id: String = "user",
        override val location: Location
    ) : MapMarker()
    
    data class PersonMarker(
        override val id: String,
        override val location: Location
    ) : MapMarker()
    
    data class EventMarker(
        override val id: String,
        override val location: Location,
        val eventType: String,
        val onClick: () -> Unit
    ) : MapMarker()
}

/**
 * Common marker styling constants
 */
object MarkerStyles {
    const val USER_MARKER_COLOR = "#4285F4" // Blue
    const val PERSON_MARKER_COLOR = "#0F9D58" // Green
    const val EVENT_MARKER_COLOR = "#EA4335" // Red
    
    const val USER_MARKER_SIZE = 20
    const val PERSON_MARKER_SIZE = 16
    const val EVENT_MARKER_SIZE = 24
    
    const val BORDER_COLOR = "#FFFFFF" // White
    const val BORDER_WIDTH = 3
}