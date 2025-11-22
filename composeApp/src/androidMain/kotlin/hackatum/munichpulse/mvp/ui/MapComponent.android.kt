package hackatum.munichpulse.mvp.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import hackatum.munichpulse.mvp.BuildConfig
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.viewmodel.MapEvent

class AndroidMapController(val viewportState: MapViewportState) : MapController {
    override fun zoomIn() {
        viewportState.flyTo(
            CameraOptions.Builder().zoom(viewportState.cameraState.zoom + 1.0).build(),
            MapAnimationOptions.Builder().duration(300).build()
        )
    }

    override fun zoomOut() {
        viewportState.flyTo(
            CameraOptions.Builder().zoom(viewportState.cameraState.zoom - 1.0).build(),
            MapAnimationOptions.Builder().duration(300).build()
        )
    }

    override fun recenter(location: Location) {
        viewportState.flyTo(
            CameraOptions.Builder().center(Point.fromLngLat(location.longitude, location.latitude)).zoom(14.0).build()
        )
    }
}

@Composable
actual fun rememberMapController(): MapController {
    val viewportState = rememberMapViewportState()
    return remember(viewportState) { AndroidMapController(viewportState) }
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
    val controller = mapController as? AndroidMapController
    val viewportState = controller?.viewportState ?: rememberMapViewportState()

    val greenDot = remember { createDotBitmap(color = android.graphics.Color.GREEN) }
    val blueDot = remember { createDotBitmap(color = android.graphics.Color.BLUE) }
    val redPin = remember { createPinBitmap(color = android.graphics.Color.RED) }

    MapboxMap(
        modifier = modifier,
        mapViewportState = viewportState
    ) {
        MapEffect(Unit) { mapView ->
            mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
        }

        // User Location (Blue Dot)
        PointAnnotation(
            point = Point.fromLngLat(userLocation.longitude, userLocation.latitude),
            iconImageBitmap = blueDot,
            iconSize = 1.5
        )

        // Other People (Green Dots)
        otherPeople.forEach { personLocation ->
            PointAnnotation(
                point = Point.fromLngLat(personLocation.longitude, personLocation.latitude),
                iconImageBitmap = greenDot,
                iconSize = 1.0
            )
        }

        // Events (Red Pins)
        events.forEach { event ->
            if (selectedFilter == "All" || event.type == selectedFilter) {
                PointAnnotation(
                    point = Point.fromLngLat(event.location.longitude, event.location.latitude),
                    iconImageBitmap = redPin,
                    iconSize = 1.5,
                    onClick = {
                        onNavigateToEvent(event.id)
                        true
                    }
                )
            }
        }
    }
}

fun createDotBitmap(color: Int): Bitmap {
    val size = 64 // px
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        this.color = color
        isAntiAlias = true
    }
    val radius = size / 2f
    canvas.drawCircle(radius, radius, radius, paint)
    
    // Add a white border
    paint.style = Paint.Style.STROKE
    paint.color = android.graphics.Color.WHITE
    paint.strokeWidth = 4f
    canvas.drawCircle(radius, radius, radius - 2f, paint)
    
    return bitmap
}

fun createPinBitmap(color: Int): Bitmap {
    val width = 64
    val height = 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        this.color = color
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    
    // Draw circle part
    val radius = width / 2f
    canvas.drawCircle(radius, radius, radius, paint)
    
    // Draw triangle part
    val path = android.graphics.Path()
    path.moveTo(0f, radius)
    path.lineTo(width.toFloat(), radius)
    path.lineTo(radius, height.toFloat())
    path.close()
    canvas.drawPath(path, paint)
    
    // Add a white border (simplified)
    paint.style = Paint.Style.STROKE
    paint.color = android.graphics.Color.WHITE
    paint.strokeWidth = 4f
    canvas.drawCircle(radius, radius, radius - 2f, paint)
    
    return bitmap
}
