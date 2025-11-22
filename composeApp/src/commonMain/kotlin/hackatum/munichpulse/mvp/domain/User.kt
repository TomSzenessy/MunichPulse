package hackatum.munichpulse.mvp.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isLocal: Boolean
)
