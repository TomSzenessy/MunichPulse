package hackatum.munichpulse.mvp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Map screen.
 * Handles map state, search queries, and event filtering.
 */
class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    /**
     * The current UI state of the map screen.
     */
    val uiState = _uiState.asStateFlow()

    /**
     * Updates the search query.
     * @param query The new search query.
     */
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Updates the selected filter.
     * @param filter The filter to apply (e.g., "All", "Music", "Food").
     */
    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}

data class MapUiState(
    val searchQuery: String = "",
    val selectedFilter: String = "All"
)
