package hackatum.munichpulse.mvp.data.repository

import hackatum.munichpulse.mvp.data.model.LogbookEntry
import hackatum.munichpulse.mvp.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing user data.
 * Note: Currently not used because ProfileViewModel loads user data directly from Firebase.
 * Kept for potential future expansion.
 */
interface UserRepository {
    /**
     * Returns a flow of the current user.
     */
    fun getCurrentUser(): Flow<User>
    
    /**
     * Returns a flow of logbook entries for the user.
     */
    fun getLogbookEntries(): Flow<List<LogbookEntry>>
}
