package hackatum.munichpulse.mvp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isLocal: Boolean
)
