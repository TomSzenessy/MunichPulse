package hackatum.munichpulse.mvp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackatum.munichpulse.mvp.data.model.Group
import hackatum.munichpulse.mvp.data.model.User
import hackatum.munichpulse.mvp.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Group screen.
 * Manages the list of groups and chat functionality.
 */
class GroupViewModel : ViewModel() {
    // Using the singleton repository directly for now as per existing code structure
    // In a proper DI setup, this would be injected
    private val repository = GroupRepository

    /**
     * A flow of groups the user belongs to.
     */
    val groups: StateFlow<List<Group>> = repository.getMyGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(GroupUiState())
    /**
     * The current UI state of the group screen.
     */
    val uiState = _uiState.asStateFlow()

    /**
     * Selects a group to view its chat.
     * @param group The group to select, or null to deselect.
     */
    fun selectGroup(group: Group?) {
        _uiState.update { it.copy(selectedGroup = group, selectedGroupEvent = null, messages = emptyList()) }
        if (group != null) {
            viewModelScope.launch {
                hackatum.munichpulse.mvp.data.repository.EventRepository().getEventById(group.eventId).collect { event ->
                    _uiState.update { it.copy(selectedGroupEvent = event) }
                }
            }
            viewModelScope.launch {
                repository.getMessages(group.eventId, group.id).collect { messages ->
                    _uiState.update { it.copy(messages = messages) }
                }
            }
        }
    }

    /**
     * Sends a message to the currently selected group.
     * @param text The message text.
     * @param user The user sending the message.
     */
    fun sendMessage(text: String, user: User) {
        val currentGroup = _uiState.value.selectedGroup
        if (currentGroup != null) {
            viewModelScope.launch {
                repository.sendMessage(currentGroup.eventId, currentGroup.id, text, user)
            }
        }
    }
}

data class GroupUiState(
    val selectedGroup: Group? = null,
    val selectedGroupEvent: hackatum.munichpulse.mvp.data.model.Event? = null,
    val messages: List<hackatum.munichpulse.mvp.data.model.ChatMessage> = emptyList()
)
