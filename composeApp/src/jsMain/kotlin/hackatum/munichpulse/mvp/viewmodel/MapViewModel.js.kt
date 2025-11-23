package hackatum.munichpulse.mvp.viewmodel

import hackatum.munichpulse.mvp.model.Location
import kotlinx.browser.window

actual object LocationGetter {
    private var currentLocation = Location(48.1351, 11.5820)

    init {
        if (js("!!window.navigator.geolocation") as Boolean) {
            window.navigator.geolocation.watchPosition(
                successCallback = { position ->
                    currentLocation = Location(
                        latitude = position.coords.latitude,
                        longitude = position.coords.longitude
                    )
                },
                errorCallback = { 
                    console.error("Error getting location: ${it.message}")
                }
            )
        }
    }

    actual fun getUserLocation(): Location {
        return currentLocation
    }
}