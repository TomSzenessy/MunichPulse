package hackatum.munichpulse.mvp.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double
)
