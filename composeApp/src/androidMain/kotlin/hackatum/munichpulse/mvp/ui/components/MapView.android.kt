package hackatum.munichpulse.mvp.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun MapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Double
) {
    // TODO: Replace with your actual Mapbox Access Token
    val mapboxAccessToken = "sk.eyJ1IjoidG9tc3oiLCJhIjoiY21pYTMxcXJ0MG9zMjJrc2UxazZuZnZydiJ9.rlkxCI0Tuj62ZNHr-HmJYA" 
    
    val mapHtml = """
        <!DOCTYPE html>
        <html>
        <head>
        <meta charset="utf-8">
        <title>Mapbox GL JS Map</title>
        <meta name="viewport" content="initial-scale=1,maximum-scale=1,user-scalable=no">
        <link href="https://api.mapbox.com/mapbox-gl-js/v3.1.2/mapbox-gl.css" rel="stylesheet">
        <script src="https://api.mapbox.com/mapbox-gl-js/v3.1.2/mapbox-gl.js"></script>
        <style>
        body { margin: 0; padding: 0; }
        #map { position: absolute; top: 0; bottom: 0; width: 100%; }
        </style>
        </head>
        <body>
        <div id="map"></div>
        <script>
            mapboxgl.accessToken = '$mapboxAccessToken';
            const map = new mapboxgl.Map({
                container: 'map',
                style: 'mapbox://styles/mapbox/dark-v11',
                center: [$longitude, $latitude],
                zoom: $zoom
            });
        </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadDataWithBaseURL(null, mapHtml, "text/html", "UTF-8", null)
            }
        }
    )
}
