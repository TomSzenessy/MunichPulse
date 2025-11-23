package hackatum.munichpulse.mvp.backend

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Tatsächliche Implementierung von GpsTracker für die JavaScript-Plattform.
 * Diese Implementierung führt keine Aktionen aus.
 */
actual class GpsTracker {
    private val _isTracking = MutableStateFlow(false)
    actual val isTracking: Flow<Boolean> = _isTracking.asStateFlow()

    actual fun startTracking() {
        // Leer, da im Browser kein Tracking stattfindet
    }

    actual fun stopTracking() {
        // Leer, da im Browser kein Tracking stattfindet
    }
}