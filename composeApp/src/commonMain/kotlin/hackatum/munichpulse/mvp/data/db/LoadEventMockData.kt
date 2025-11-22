import hackatum.munichpulse.mvp.data.model.Event
import kotlinx.serialization.json.Json
import munichpulse.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

class LoadEventMockData {

    companion object {
        @OptIn(ExperimentalResourceApi::class)
        suspend fun loadEventsFromResources(filePath: String): List<Event> {
            try {
                // Lädt die Datei aus den gemeinsamen Ressourcen
                val bytes = Res.readBytes(filePath)
                val jsonString = bytes.decodeToString()

                val json = Json { ignoreUnknownKeys = true }
                return json.decodeFromString(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                return emptyList()
            }
        }
    }
}