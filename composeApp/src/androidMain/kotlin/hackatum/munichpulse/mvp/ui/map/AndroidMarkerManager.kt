package hackatum.munichpulse.mvp.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.viewmodel.MapEvent

/**
 * Android implementation of MarkerManager using Mapbox Compose
 */
class AndroidMarkerManager {
    // Bitmaps for different marker types
    val userMarkerBitmap: Bitmap = createDotBitmap(color = android.graphics.Color.parseColor(MarkerStyles.USER_MARKER_COLOR))
    val personMarkerBitmap: Bitmap = createDotBitmap(color = android.graphics.Color.parseColor(MarkerStyles.PERSON_MARKER_COLOR))
    val eventMarkerBitmap: Bitmap = createPinBitmap(color = android.graphics.Color.parseColor(MarkerStyles.EVENT_MARKER_COLOR))

    /**
     * Creates PointAnnotation for user location
     */
    fun createUserAnnotation(location: Location): @Composable () -> Unit = {
        PointAnnotation(
            point = Point.fromLngLat(location.longitude, location.latitude),
            iconImageBitmap = userMarkerBitmap,
            iconSize = 1.5
        )
    }

    /**
     * Creates PointAnnotations for other people
     */
    fun createPersonAnnotations(people: List<Location>): @Composable () -> Unit = {
        people.forEach { personLocation ->
            PointAnnotation(
                point = Point.fromLngLat(personLocation.longitude, personLocation.latitude),
                iconImageBitmap = personMarkerBitmap,
                iconSize = 1.0
            )
        }
    }

    /**
     * Creates PointAnnotations for events
     */
    fun createEventAnnotations(
        events: List<MapEvent>,
        selectedFilter: String,
        onNavigateToEvent: (String) -> Unit
    ): @Composable () -> Unit = {
        events.filter { selectedFilter == "All" || it.type == selectedFilter }.forEach { event ->
            PointAnnotation(
                point = Point.fromLngLat(event.location.longitude, event.location.latitude),
                iconImageBitmap = eventMarkerBitmap,
                iconSize = 1.5,
                onClick = {
                    onNavigateToEvent(event.id)
                    true
                }
            )
        }
    }

    private fun createDotBitmap(color: Int): Bitmap {
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

    private fun createPinBitmap(color: Int): Bitmap {
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
}