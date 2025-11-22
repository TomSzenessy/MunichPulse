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
    events: List<MapEvent>,
    selectedFilter: String,
    onNavigateToEvent: (String) -> Unit
) {
    val controller = mapController as? AndroidMapController
    val viewportState = controller?.viewportState ?: rememberMapViewportState()

    val greenDot = remember { createDotBitmap(color = android.graphics.Color.GREEN) }
    val blueDot = remember { createDotBitmap(color = android.graphics.Color.BLUE) }

    MapboxMap(
        modifier = modifier,
        mapViewportState = viewportState,
    ) {
        MapEffect(Unit) { mapView ->
            mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
        }

        PointAnnotation(
            point = Point.fromLngLat(userLocation.longitude, userLocation.latitude),
            iconImageBitmap = blueDot,
            iconSize = 1.5
        )

        events.forEach { event ->
            if (selectedFilter == "All" || event.type == selectedFilter) {
                PointAnnotation(
                    point = Point.fromLngLat(event.location.longitude, event.location.latitude),
                    iconImageBitmap = greenDot,
                    iconSize = 1.2,
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
        style = Paint.Style.FILL
    }
    val strokePaint = Paint().apply {
        this.color = android.graphics.Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    
    val radius = size / 2f
    canvas.drawCircle(radius, radius, radius - 4, paint)
    canvas.drawCircle(radius, radius, radius - 4, strokePaint)
    return bitmap
}
