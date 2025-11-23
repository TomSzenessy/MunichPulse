package hackatum.munichpulse.mvp.data.model

import hackatum.munichpulse.mvp.backend.FirebaseInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String,
    val title: String,
    val location: String,
    val imageUrl: String,
    val fullnessPercentage: Int,
    val isTrending: Boolean = false,
    val startTime: String
)

object eventFuncs {
    // Die Flow-Funktion, die die Suspend-Funktion umwickelt
    fun getAllEventsFlow(): Flow<List<Event>> = flow {
        // 1. Initialen "leeren Flow" (leere Liste) emittieren
        //    Dies ist nützlich für den UI-Zustand (z.B. Ladeanzeige anzeigen)
        emit(emptyList())

        // 2. Die suspend Funktion sicher aufrufen und auf ihr Ergebnis warten
        val events: List<Event> = FirebaseInterface.getInstance().getAllEvents()

        // 3. Das tatsächliche Ergebnis emittieren
        emit(events)
    }
}


