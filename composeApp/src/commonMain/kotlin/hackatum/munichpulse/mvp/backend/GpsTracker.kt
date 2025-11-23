package hackatum.munichpulse.mvp.backend
import hackatum.munichpulse.mvp.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * Erwartete Klasse für das GPS-Tracking.
 * Definiert die gemeinsame Schnittstelle für plattformspezifische Implementierungen.
 */
expect class GpsTracker {
    /**
     * Startet das GPS-Tracking.
     */
    fun startTracking()

    fun getLocation(): Location

    /**
     * Stoppt das GPS-Tracking.
     */
    fun stopTracking()

    fun locationListenerCallback(location: Location)

        /**
     * Gibt an, ob das Tracking gerade aktiv ist.
     */
    var isTracking: Boolean
}