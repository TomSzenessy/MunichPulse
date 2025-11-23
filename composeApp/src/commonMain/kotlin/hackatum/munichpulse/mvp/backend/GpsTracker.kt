package hackatum.munichpulse.mvp.backend
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

    /**
     * Stoppt das GPS-Tracking.
     */
    fun stopTracking()

    /**
     * Gibt an, ob das Tracking gerade aktiv ist.
     */
    val isTracking: Flow<Boolean>
}