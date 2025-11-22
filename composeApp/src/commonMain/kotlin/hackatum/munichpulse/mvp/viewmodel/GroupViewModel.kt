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
        _uiState.update { it.copy(selectedGroup = group) }
    }

    /**
     * Sends a message to the currently selected group.
     * @param text The message text.
     * @param user The user sending the message.
     */
    fun sendMessage(text: String, user: User) {
        val currentGroup = _uiState.value.selectedGroup
        if (currentGroup != null) {
            repository.sendMessage(currentGroup.id, text, user)
            // Optimistically update selected group or rely on flow update
            // Since groups flow updates, we need to re-select the group from the list to get new messages
            // This logic is handled in the UI currently, but should be here.
            // For now, we rely on the repository updating the flow.
        }
    }
}

data class GroupUiState(
    val selectedGroup: Group? = null
)
