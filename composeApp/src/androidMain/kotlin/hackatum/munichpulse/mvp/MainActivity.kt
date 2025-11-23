package hackatum.munichpulse.mvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import hackatum.munichpulse.mvp.ui.LoginScreen
import hackatum.munichpulse.mvp.ui.ProvideAppStrings
import hackatum.munichpulse.mvp.ui.theme.UrbanPulseTheme

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import hackatum.munichpulse.mvp.backend.GpsTracker
import kotlin.collections.mapIndexed

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        //val database = getDatabase(AndroidPlatformContext(applicationContext))

        setContent {
            App()
            //UrbanPulseTheme { GpsTrackerScreen() }
        }
    }

    val REQUEST_TRACKING_PERMISSIONS: Int = 1

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode != REQUEST_TRACKING_PERMISSIONS) {
            permissions.mapIndexed { i, perm ->
                if (perm == Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        trackingPermissions = true
                    } else {
                        trackingPermissions = false
                    }
                }
            }
        }
    }
}

var trackingPermissions = false

@Preview
@Composable
fun AppAndroidPreview() {
    ProvideAppStrings {
        UrbanPulseTheme {
            LoginScreen(onLoginSuccess = { _, _ -> })
        }
    }
}



@Composable
fun GpsTrackerScreen() {
    val context = LocalContext.current
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    val locationCallback = { location: Location -> lastLocation = location }
    val gpsTracker = remember { GpsTracker(context, locationCallback) }
    val isTracking by gpsTracker.isTracking.collectAsState(initial = false)

    // Berechtigungen anfordern
//    LaunchedEffect(Unit) {
//        ActivityCompat.requestPermissions(
//            context as Activity,
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//            101
//        )
//    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "GPS Tracking Test",
            style = MaterialTheme.typography.headlineMedium
        ) // Stilnamen können in M3 anders sein
        Spacer(modifier = Modifier.height(20.dp))

        if (isTracking) {
            Button(onClick = { gpsTracker.stopTracking() }) {
                Text("Stop Tracking")
            }
        } else {
            Button(onClick = {
                gpsTracker.startTracking()
                //gpsTracker.startTracking()

            }) {
                Text("Start Tracking")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Text("Status: ${if (isTracking) "Tracking aktiv" else "Gestoppt"}")
        // ... restliche UI

        Spacer(modifier = Modifier.height(8.dp))
        lastLocation?.let {
            Text("Lat: ${it.latitude}")
            Text("Lon: ${it.longitude}")
        } ?: Text("Warte auf Standort...")
    }
}
