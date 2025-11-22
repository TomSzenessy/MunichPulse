package hackatum.munichpulse.mvp.ui.components

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
actual fun MapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Double
) {
    // Fallback for Web/JS since embedding iframe in Canvas is complex
    // Using a static image or the previous placeholder
    // Basic Zoomable/Pannable Image for Web
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .clipToBounds()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offset += pan
                }
            }
    ) {
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCgMGykCQJc76lxR3xwrkfmW2Wh9o-q4H854fQlHptaGbvqR9_X9ZlAL0Q4DMKj7SGfHZxsKKqmpXiRAWxln6rI2aDL8TtqATOsGBSjuFjMtOlFf7M6VswXKYtQbyYvplXY17tWXH1c1gAz2GH9C9kl1UaKkfOMm1U4-0eBBAWIMFUjlL8qnj3sUv2p62Fd81wYVMlHjyeuMV7-oHQOnBRSS46nT3E367yldCVg6EsmzaB2NRe5HaAp3xm6SHBAFS9oVXpvp3WJcN48",
            contentDescription = "Map Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }
}
