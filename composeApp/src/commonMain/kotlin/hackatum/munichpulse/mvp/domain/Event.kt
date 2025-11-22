package hackatum.munichpulse.mvp.domain

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String,
    val title: String,
    val location: String,
    val imageUrl: String,
    val fullnessPercentage: Int,
    val isTrending: Boolean = false
)
