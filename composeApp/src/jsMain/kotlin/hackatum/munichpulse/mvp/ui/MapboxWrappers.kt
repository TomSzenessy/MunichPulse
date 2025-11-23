package hackatum.munichpulse.mvp.ui

import org.w3c.dom.HTMLElement

@JsModule("mapbox-gl")
@JsNonModule
external object MapboxGl {
    var accessToken: String
    class Map(options: dynamic) {
        fun on(event: String, callback: (dynamic) -> Unit)
        fun remove()
        fun flyTo(options: dynamic)
        fun resize()
        fun getZoom(): Double
    }
    class Marker(options: dynamic) {
        fun setLngLat(lngLat: Array<Double>): Marker
        fun addTo(map: Map): Marker
        fun remove()
        fun getElement(): HTMLElement
    }
}
