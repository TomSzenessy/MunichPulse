package hackatum.munichpulse.mvp.viewmodel

import androidx.lifecycle.ViewModel
import hackatum.munichpulse.mvp.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MapEvent(
    val id: String,
    val title: String,
    val location: Location,
    val type: String = "Music"
)

data class MapUiState(
    val searchQuery: String = "",
    val selectedFilter: String = "All",
    val userLocation: Location = Location(48.262333, 11.667861), // Garching Forschungszentrum Mathe/Informatik
    val otherPeople: List<Location> = listOf(
        Location(48.1360, 11.5830),
        Location(48.1345, 11.5810),
        Location(48.1355, 11.5840)
    ),
    val events: List<MapEvent> = listOf(
        MapEvent("1", "Jazz Night", Location(48.137154, 11.576124), "Music"),
        MapEvent("2", "Food Truck Festival", Location(48.140000, 11.590000), "Food"),
        MapEvent("3", "Tech Hackathon", Location(48.150000, 11.560000), "Tech")
    )
)

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}