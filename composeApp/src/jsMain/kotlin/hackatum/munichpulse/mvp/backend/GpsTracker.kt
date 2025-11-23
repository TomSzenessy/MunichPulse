package hackatum.munichpulse.mvp.backend

import hackatum.munichpulse.mvp.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Tatsächliche Implementierung von GpsTracker für die JavaScript-Plattform.
 * Diese Implementierung führt keine Aktionen aus.
 */
actual class GpsTracker {
    actual var isTracking = false

    actual fun startTracking() {
        // Leer, da im Browser kein Tracking stattfindet
    }

    actual fun stopTracking() {
        // Leer, da im Browser kein Tracking stattfindet
    }

    actual fun getLocation(): Location {
        return Location(0.0, 0.0)
    }

    actual fun locationListenerCallback(location: Location) {
    }
}