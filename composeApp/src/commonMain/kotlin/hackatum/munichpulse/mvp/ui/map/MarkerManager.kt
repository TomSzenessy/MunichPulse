package hackatum.munichpulse.mvp.ui.map

/**
 * Common interface for managing map markers across platforms
 */
interface MarkerManager {
    /**
     * Updates the user location marker
     */
    fun updateUserLocation(location: hackatum.munichpulse.mvp.model.Location)
    
    /**
     * Updates other people markers
     */
    fun updateOtherPeople(people: List<hackatum.munichpulse.mvp.model.Location>)
    
    /**
     * Updates event markers with filtering
     */
    fun updateEvents(
        events: List<hackatum.munichpulse.mvp.viewmodel.MapEvent>,
        selectedFilter: String,
        onNavigateToEvent: (String) -> Unit
    )
    
    /**
     * Clears all markers
     */
    fun clearAll()
}