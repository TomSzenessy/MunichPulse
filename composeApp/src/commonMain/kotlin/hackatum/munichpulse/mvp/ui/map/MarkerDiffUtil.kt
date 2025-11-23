package hackatum.munichpulse.mvp.ui.map

import hackatum.munichpulse.mvp.model.Location
import hackatum.munichpulse.mvp.viewmodel.MapEvent

/**
 * Utility class for calculating differences between marker states
 */
object MarkerDiffUtil {
    
    /**
     * Calculates the difference between current and new marker states
     */
    fun calculateDiff(
        oldState: MapState,
        newState: MapState
    ): MarkerDiffResult {
        val userLocationDiff = calculateUserLocationDiff(oldState.userLocation, newState.userLocation)
        val peopleDiff = calculatePeopleDiff(oldState.otherPeople, newState.otherPeople)
        val eventsDiff = calculateEventsDiff(oldState.events, newState.events, oldState.selectedFilter, newState.selectedFilter)
        
        return MarkerDiffResult(
            userLocationDiff = userLocationDiff,
            peopleDiff = peopleDiff,
            eventsDiff = eventsDiff
        )
    }
    
    private fun calculateUserLocationDiff(
        old: Location?,
        new: Location?
    ): UserLocationDiff {
        return when {
            old == null && new != null -> UserLocationDiff.ADDED(new)
            old != null && new == null -> UserLocationDiff.REMOVED
            old != null && new != null && old != new -> UserLocationDiff.UPDATED(new)
            else -> UserLocationDiff.NONE
        }
    }
    
    private fun calculatePeopleDiff(
        old: List<Location>,
        new: List<Location>
    ): PeopleDiff {
        val oldSet = old.toSet()
        val newSet = new.toSet()
        
        val added = newSet - oldSet
        val removed = oldSet - newSet
        
        return PeopleDiff(
            added = added.toList(),
            removed = removed.toList(),
            unchanged = (oldSet intersect newSet).toList()
        )
    }
    
    private fun calculateEventsDiff(
        old: List<MapEvent>,
        new: List<MapEvent>,
        oldFilter: String,
        newFilter: String
    ): EventsDiff {
        // Filter events based on the respective filters
        val oldFiltered = old.filter { oldFilter == "All" || it.type == oldFilter }
        val newFiltered = new.filter { newFilter == "All" || it.type == newFilter }
        
        val oldMap = oldFiltered.associateBy { it.id }
        val newMap = newFiltered.associateBy { it.id }
        
        val added = (newMap.keys - oldMap.keys).map { newMap[it]!! }
        val removed = (oldMap.keys - newMap.keys).map { oldMap[it]!! }
        val updated = (oldMap.keys intersect newMap.keys)
            .map { id -> oldMap[id]!! to newMap[id]!! }
            .filter { (old, new) -> old != new }
        
        return EventsDiff(
            added = added,
            removed = removed,
            updated = updated,
            unchanged = (oldMap.keys intersect newMap.keys)
                .map { id -> oldMap[id]!! }
                .filter { event -> !updated.any { it.first.id == event.id } }
        )
    }
}

/**
 * Result of marker diff calculation
 */
data class MarkerDiffResult(
    val userLocationDiff: UserLocationDiff,
    val peopleDiff: PeopleDiff,
    val eventsDiff: EventsDiff
)

/**
 * User location diff result
 */
sealed class UserLocationDiff {
    object NONE : UserLocationDiff()
    data class ADDED(val location: Location) : UserLocationDiff()
    data class UPDATED(val location: Location) : UserLocationDiff()
    object REMOVED : UserLocationDiff()
}

/**
 * People markers diff result
 */
data class PeopleDiff(
    val added: List<Location>,
    val removed: List<Location>,
    val unchanged: List<Location>
)

/**
 * Event markers diff result
 */
data class EventsDiff(
    val added: List<MapEvent>,
    val removed: List<MapEvent>,
    val updated: List<Pair<MapEvent, MapEvent>>,
    val unchanged: List<MapEvent>
)