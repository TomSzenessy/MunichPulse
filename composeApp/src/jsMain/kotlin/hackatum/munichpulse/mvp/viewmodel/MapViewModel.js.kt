package hackatum.munichpulse.mvp.viewmodel

import hackatum.munichpulse.mvp.model.Location

actual object LocationGetter {
    actual fun getUserLocation(): Location {
        return Location(48.1351, 11.5820)
    }
}