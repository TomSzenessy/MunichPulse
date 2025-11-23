package hackatum.munichpulse.mvp.ui

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
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.ui.map.AndroidMapRenderer
import hackatum.munichpulse.mvp.ui.map.MapRenderer
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
actual fun MapRenderer(): MapRenderer {
    return remember { AndroidMapRenderer() }
}
