package hackatum.munichpulse.mvp.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackatum.munichpulse.mvp.data.model.Event
import hackatum.munichpulse.mvp.data.repository.EventRepository
import hackatum.munichpulse.mvp.data.repository.MockEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Home screen.
 * Manages the data for trending, nearby, and all events.
 * @property repository The repository to fetch event data from.
 */
class HomeViewModel(
    private val repository: EventRepository = MockEventRepository()
) : ViewModel() {

    /**
     * A flow of trending events.
     */
    val trendingEvents: StateFlow<List<Event>> = repository.getTrendingEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * A flow of nearby events.
     */
    val nearbyEvents: StateFlow<List<Event>> = repository.getNearbyEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * A flow of all available events.
     */
    val allEvents: StateFlow<List<Event>> = repository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
