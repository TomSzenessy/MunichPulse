package hackatum.munichpulse.mvp.ui.map

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
import hackatum.munichpulse.mvp.viewmodel.MapEvent

/**
 * Android implementation of MapRenderer using Mapbox Compose
 */
class AndroidMapRenderer : MapRenderer {
    
    @Composable
    override fun renderMap(
        state: MapState,
        mapController: MapController,
        onMarkerClick: (String) -> Unit,
        modifier: Modifier
    ) {
        // Create and remember the marker manager
        val markerManager = remember { AndroidMarkerManager() }
        
        // Create viewport state for camera control
        val viewportState = if (mapController is hackatum.munichpulse.mvp.ui.AndroidMapController) {
            mapController.viewportState
        } else {
            rememberMapViewportState()
        }

        
        // Apply camera position changes
        state.cameraPosition?.let { camera ->
            state.userLocation?.let { location ->
            viewportState.flyTo(
                CameraOptions.Builder()
                .center(Point.fromLngLat(location.longitude, location.latitude))
                .zoom(camera.zoom)
                .build(),
                MapAnimationOptions.Builder().duration(300).build()
            )
            }
        }

        MapboxMap(
            modifier = modifier,
            mapViewportState = viewportState
        ) {
            MapEffect(Unit) { mapView ->
                mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
            }

            // User Location (Blue Dot)
            state.userLocation?.let { location ->
                markerManager.createUserAnnotation(location)()
            }

            // Other People (Green Dots)
            markerManager.createPersonAnnotations(state.otherPeople)()

            // Events (Red Pins)
            markerManager.createEventAnnotations(state.events, state.selectedFilter, onMarkerClick)()
        }
    }
}