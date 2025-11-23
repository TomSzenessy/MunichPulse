package hackatum.munichpulse.mvp.backend

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


const val REQUEST_TRACKING_PERMISSIONS: Int = 1


/**
 * Tatsächliche Implementierung von GpsTracker für die Android-Plattform.
 * Diese Klasse verfolgt den Standort des Geräts und speichert die Daten in einer GPX-Datei.
 *
 * @param context Der Anwendung- oder Activity-Kontext, der für den Zugriff auf Systemdienste benötigt wird.
 * @param locationUpdateCallback Eine Callback-Funktion, die bei jeder neuen Standortaktualisierung aufgerufen wird.
 */
actual class GpsTracker(
    private val context: Context,
    private val locationUpdateCallback: (Location) -> Unit
) {
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var gpxFile: File? = null
    private var fileWriter: FileWriter? = null

    private val _isTracking = MutableStateFlow(false)
    actual val isTracking: Flow<Boolean> = _isTracking.asStateFlow()

    // LocationListener, der die empfangenen Standortdaten verarbeitet
    private val locationListener: LocationListener = LocationListener { location ->
        // Ruft den externen Callback auf
        locationUpdateCallback(location)
        // Fügt den neuen Punkt zur GPX-Datei hinzu
        addTrackpoint(location)
    }

    /**
     * Startet das GPS-Tracking.
     * Prüft die Berechtigungen, erstellt eine neue GPX-Datei und registriert den LocationListener.
     */
    @SuppressLint("MissingPermission")
    actual fun startTracking() {
        // Überprüft, ob die Berechtigung für den genauen Standort erteilt wurde
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_TRACKING_PERMISSIONS)

            // In einer produktiven App sollte hier eine Berechtigungsanfrage erfolgen.
            // Zum Beispiel, indem eine Exception geworfen oder ein Fehlerzustand signalisiert wird.
            println("GPS-Tracking kann nicht gestartet werden: Keine Berechtigung.")
            return
        }

        if (_isTracking.value) return // Verhindert mehrfaches Starten

        try {
            gpxFile = createGpxFile()
            fileWriter = FileWriter(gpxFile, true)
            fileWriter?.append(getGpxHeader())

            // Fordert Standort-Updates vom GPS-Anbieter an
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L, // Mindestzeitintervall in Millisekunden (5 Sek.)
                5f,    // Mindestdistanz in Metern (5 Meter)
                locationListener,
                Looper.getMainLooper() // Stellt sicher, dass der Listener auf dem Haupt-Thread läuft
            )
            _isTracking.value = true
        } catch (e: Exception) {
            e.printStackTrace()
            // Hier könnte eine robustere Fehlerbehandlung implementiert werden
            stopTracking() // Aufräumen im Fehlerfall
        }
    }

    /**
     * Stoppt das GPS-Tracking.
     * Deregistriert den LocationListener und schließt die GPX-Datei.
     */
    actual fun stopTracking() {
        if (!_isTracking.value) return

        locationManager.removeUpdates(locationListener)
        try {
            fileWriter?.append(getGpxFooter())
            fileWriter?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Setzt die Zustände zurück
            fileWriter = null
            gpxFile = null
            _isTracking.value = false
        }
    }

    /**
     * Erstellt eine neue GPX-Datei mit einem eindeutigen Zeitstempel im internen Speicher der App. [1, 12]
     */
    private fun createGpxFile(): File {
        val gpxDir = File(context.filesDir, "gpx")
        if (!gpxDir.exists()) {
            gpxDir.mkdirs()
        }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(gpxDir, "track_$timestamp.gpx")
    }

    /**
     * Gibt den XML-Header für eine GPX-Datei zurück. [2, 8]
     */
    private fun getGpxHeader(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="no" ?>
<gpx version="1.1" creator="MunichPulse">
<trk>
<name>Track ${gpxFile?.nameWithoutExtension ?: ""}</name>
<trkseg>
"""
    }

    /**
     * Formatiert einen Standort als GPX-Trackpoint und fügt ihn der Datei hinzu. [3, 9]
     */
    private fun addTrackpoint(location: Location) {
        // Zeitstempel im ISO 8601 Format, wie es für GPX empfohlen wird. [2, 11]
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val time = isoFormat.format(Date(location.time))
        val trackpoint = """
<trkpt lat="${location.latitude}" lon="${location.longitude}">
  <ele>${location.altitude}</ele>
  <time>$time</time>
</trkpt>
"""
        try {
            fileWriter?.append(trackpoint)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Gibt den XML-Footer für eine GPX-Datei zurück.
     */
    private fun getGpxFooter(): String {
        return """
</trkseg>
</trk>
</gpx>
"""
    }
}
