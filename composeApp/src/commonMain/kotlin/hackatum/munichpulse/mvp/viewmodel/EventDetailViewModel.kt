package hackatum.munichpulse.mvp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackatum.munichpulse.mvp.DataController
import hackatum.munichpulse.mvp.data.model.Event
import hackatum.munichpulse.mvp.data.repository.EventRepository
import hackatum.munichpulse.mvp.data.repository.MockEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val repository: EventRepository = MockEventRepository()
) : ViewModel() {

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            repository.getEventById(eventId).collect {
                _event.value = it
            }
        }
    }

    /**
     * Add a user to a specific group for this event (delegates to DataController -> Firebase).
     */
    fun addUserToSpecificGroup(eventId: String, groupId: String) {
        viewModelScope.launch {
            DataController.getInstance().addUserToEventGroup(eventId, groupId)
        }
    }
}
