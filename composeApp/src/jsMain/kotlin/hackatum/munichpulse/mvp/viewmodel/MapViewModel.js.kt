package hackatum.munichpulse.mvp.viewmodel

import hackatum.munichpulse.mvp.model.Location
import kotlinx.browser.window

actual object LocationGetter {
    private var currentLocation = Location(48.1351, 11.5820)

    init {
        if (js("!!window.navigator.geolocation") as Boolean) {
            val navigator = window.navigator.asDynamic()
            navigator.geolocation.watchPosition(
                { position: dynamic ->
                    currentLocation = Location(
                        latitude = position.coords.latitude as Double,
                        longitude = position.coords.longitude as Double
                    )
                },
                { error: dynamic -> 
                    console.error("Error getting location: ${error.message}")
                }
            )
        }
    }

    actual fun getUserLocation(): Location {
        return currentLocation
    }
}