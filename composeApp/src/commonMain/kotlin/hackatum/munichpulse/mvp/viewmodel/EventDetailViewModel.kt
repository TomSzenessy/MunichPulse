package hackatum.munichpulse.mvp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackatum.munichpulse.mvp.DataController
import hackatum.munichpulse.mvp.data.model.Event
import hackatum.munichpulse.mvp.data.repository.EventRepository
import hackatum.munichpulse.mvp.data.repository.MockEventRepository
import hackatum.munichpulse.mvp.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val repository: EventRepository = MockEventRepository()
) : ViewModel() {

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    private val _userGroups = GroupRepository.groups
    
    /**
     * The group the user is currently in for this event, if any.
     */
    val currentGroupForEvent: StateFlow<hackatum.munichpulse.mvp.data.model.Group?> = 
        kotlinx.coroutines.flow.combine(_event, _userGroups) { event, groups ->
            if (event == null) null else groups.find { it.eventId == event.id }
        }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), null)

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            repository.getEventById(eventId).collect {
                _event.value = it
            }
        }
    }

    /**
     * Add a user to a random group for this event.
     */
    fun addUserToEventGroup(eventId: String) {
        viewModelScope.launch {
            GroupRepository.joinGroup(eventId)
        }
    }

    /**
     * Add a user to an individual people group for this event (delegates to DataController -> Firebase).
     */
    fun addUserToIndividualEventGroup(eventId: String) {
        viewModelScope.launch {
            DataController.getInstance().addUserToEventIndividuals(eventId)
        }
    }
    
    fun leaveGroup(eventId: String, groupId: String) {
        viewModelScope.launch {
            GroupRepository.leaveGroup(eventId, groupId)
        }
    }
}
