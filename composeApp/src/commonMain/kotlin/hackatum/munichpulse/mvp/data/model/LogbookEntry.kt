package hackatum.munichpulse.mvp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LogbookEntry(
    val id: String,
    val locationName: String,
    val distanceTraveled: Double, // in km
    val crowdContribution: Int,
    val timestamp: Long
)
